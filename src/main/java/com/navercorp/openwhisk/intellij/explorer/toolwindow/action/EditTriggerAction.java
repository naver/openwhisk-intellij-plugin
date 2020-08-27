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
import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import com.navercorp.openwhisk.intellij.explorer.dialog.trigger.TriggerManagerDialog;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static com.intellij.icons.AllIcons.Actions.Edit;

public class EditTriggerAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(EditTriggerAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskAuth whiskAuth;
    private WhiskTriggerMetaData whiskTriggerMetaData;

    private WhiskTriggerService whiskTriggerService = WhiskTriggerService.getInstance();
    private WhiskActionService whiskActionService = WhiskActionService.getInstance();

    public EditTriggerAction(WhiskAuth whiskAuth, WhiskTriggerMetaData whiskTriggerMetaData) {
        super("Edit", "Edit trigger", Edit);
        this.whiskAuth = whiskAuth;
        this.whiskTriggerMetaData = whiskTriggerMetaData;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null && whiskTriggerMetaData != null) {
            try {
                whiskTriggerService.getWhiskTrigger(whiskAuth, whiskTriggerMetaData.getName())
                        .ifPresent(executableWhiskTrigger -> openEditTriggerDialog(e.getProject(), executableWhiskTrigger));
            } catch (IOException ex) {
                final String msg = "The trigger cannot be loaded: " + whiskTriggerMetaData.getName();
                LOG.error(msg, ex);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
            }
        }
    }

    private void openEditTriggerDialog(Project project, ExecutableWhiskTrigger executableWhiskTrigger) {
        try {
            List<WhiskActionMetaData> actions = whiskActionService.getWhiskActions(whiskAuth);
            TriggerManagerDialog dialog = new TriggerManagerDialog(project, whiskAuth, executableWhiskTrigger, actions);
            if (dialog.showAndGet()) {
                LOG.info("TriggerManagerDialog is closed");
            }
        } catch (IOException ex) {
            final String msg = "The trigger cannot be loaded: " + whiskTriggerMetaData.getName();
            LOG.error(msg, ex);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }
}
