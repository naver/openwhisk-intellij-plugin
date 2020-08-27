/**
 * Copyright 2020-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.openwhisk.intellij.explorer.toolwindow.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.explorer.dialog.action.DeleteActionDialog;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.intellij.icons.AllIcons.Actions.GC;

public class DeleteActionAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(DeleteActionAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskActionMetaData whiskActionMetaData;
    private WhiskAuth whiskAuth;
    private WhiskActionService whiskActionService;

    public DeleteActionAction(WhiskAuth whiskAuth, WhiskActionMetaData whiskActionMetaData) {
        super("Delete", "Delete action", GC);
        this.whiskActionMetaData = whiskActionMetaData;
        this.whiskAuth = whiskAuth;
        this.whiskActionService = WhiskActionService.getInstance();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null && whiskActionMetaData != null) {
            if ((new DeleteActionDialog(e.getProject())).showAndGet()) {
                try {
                    whiskActionService.deleteWhiskActions(whiskAuth, whiskActionMetaData.getWhiskPackage(), whiskActionMetaData.getName())
                            .ifPresent(deletedAction -> {
                                String msg = deletedAction.getName() + " action is deleted";
                                LOG.info(msg);
                                NOTIFIER.notify(e.getProject(), msg, NotificationType.INFORMATION);

                                ActionManager.getInstance().getAction("WhiskExplorer.Actions.Controls.Refresh").actionPerformed(e);
                            });
                } catch (IOException ex) {
                    final String msg = "The action cannot be deleted: " + whiskActionMetaData.getName();
                    LOG.error(msg, ex);
                    NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
                }
            }
        }
    }
}
