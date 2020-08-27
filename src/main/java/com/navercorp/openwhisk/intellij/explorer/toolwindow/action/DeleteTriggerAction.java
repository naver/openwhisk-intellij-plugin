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
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import com.navercorp.openwhisk.intellij.explorer.dialog.trigger.DeleteTriggerDialog;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.intellij.icons.AllIcons.Actions.GC;

public class DeleteTriggerAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(DeleteTriggerAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskTriggerMetaData whiskTriggerMetaData;
    private WhiskAuth whiskAuth;

    private WhiskTriggerService whiskTriggerService;

    public DeleteTriggerAction(WhiskAuth whiskAuth, WhiskTriggerMetaData whiskTriggerMetaData) {
        super("Delete", "Delete trigger", GC);
        this.whiskTriggerMetaData = whiskTriggerMetaData;
        this.whiskAuth = whiskAuth;
        this.whiskTriggerService = WhiskTriggerService.getInstance();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null && whiskTriggerMetaData != null) {
            if ((new DeleteTriggerDialog(e.getProject(), whiskTriggerMetaData)).showAndGet()) {
                try {
                    whiskTriggerService.deleteWhiskTrigger(whiskAuth, whiskTriggerMetaData.getName()).ifPresent(deletedTrigger -> {
                        String msg = deletedTrigger.getName() + " trigger is deleted";
                        LOG.info(msg);
                        NOTIFIER.notify(e.getProject(), msg, NotificationType.INFORMATION);
                    });
                    ActionManager.getInstance().getAction("WhiskExplorer.Actions.Controls.Refresh").actionPerformed(e);
                } catch (IOException ex) {
                    final String msg = "The trigger cannot be deleted: " + whiskTriggerMetaData.getName();
                    LOG.error(msg, ex);
                    NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
                }
            }
        }
    }
}
