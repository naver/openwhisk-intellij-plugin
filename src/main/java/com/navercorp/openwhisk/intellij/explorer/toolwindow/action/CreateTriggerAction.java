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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.explorer.dialog.trigger.TriggerCreationManagerDialog;
import org.jetbrains.annotations.NotNull;

import static com.intellij.icons.AllIcons.General.Add;

public class CreateTriggerAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(CreateTriggerAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskAuth whiskAuth;

    public CreateTriggerAction(WhiskAuth whiskAuth) {
        super("Create Trigger", "Create trigger", Add);
        this.whiskAuth = whiskAuth;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null) {
            TriggerCreationManagerDialog dialog = new TriggerCreationManagerDialog(e.getProject(), whiskAuth);
            if (dialog.showAndGet()) {
                LOG.info("TriggerManagerDialog is closed");
            }
        }
    }
}
