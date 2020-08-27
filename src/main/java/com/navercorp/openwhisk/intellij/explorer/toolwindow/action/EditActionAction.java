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
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.explorer.dialog.action.ActionManagerDialog;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.intellij.icons.AllIcons.Actions.Edit;

public class EditActionAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(EditActionAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskAuth whiskAuth;
    private WhiskActionMetaData whiskActionMetaData;

    private WhiskActionService whiskActionService = WhiskActionService.getInstance();

    public EditActionAction(WhiskAuth whiskAuth, WhiskActionMetaData whiskActionMetaData) {
        super("Edit", "Edit Action", Edit);
        this.whiskAuth = whiskAuth;
        this.whiskActionMetaData = whiskActionMetaData;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null && whiskActionMetaData != null) {
            try {
                List<WhiskActionMetaData> actions = whiskActionService.getWhiskActions(whiskAuth);
                whiskActionService.getWhiskAction(whiskAuth,
                        Optional.of(whiskActionMetaData.getNamespacePath()),
                        whiskActionMetaData.getWhiskPackage(),
                        whiskActionMetaData.getName())
                        .ifPresent(action -> {
                            ActionManagerDialog dialog = new ActionManagerDialog(e.getProject(), whiskAuth, action, actions);
                            if (dialog.showAndGet()) {
                                LOG.info("ActionManagerDialog is closed");
                            }
                        });
            } catch (IOException ex) {
                final String msg = "The action cannot be loaded: " + whiskActionMetaData.getFullyQualifiedName();
                LOG.error(msg, ex);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
            }
        }
    }
}
