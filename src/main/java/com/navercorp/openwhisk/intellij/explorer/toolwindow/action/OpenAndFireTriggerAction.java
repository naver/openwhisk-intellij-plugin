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
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import com.navercorp.openwhisk.intellij.run.toolwindow.listener.OpenAndFireTriggerControlActionListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.intellij.icons.AllIcons.Actions.Execute;

public class OpenAndFireTriggerAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(OpenAndFireTriggerAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskTriggerMetaData whiskTriggerMetaData;
    private WhiskAuth whiskAuth;

    private WhiskTriggerService whiskTriggerService;

    public OpenAndFireTriggerAction(WhiskAuth whiskAuth, WhiskTriggerMetaData whiskTriggerMetaData) {
        super("Fire", "Fire trigger", Execute);
        this.whiskTriggerMetaData = whiskTriggerMetaData;
        this.whiskAuth = whiskAuth;
        this.whiskTriggerService = WhiskTriggerService.getInstance();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null && whiskTriggerMetaData != null) {
            try {
                whiskTriggerService.getWhiskTrigger(whiskAuth, whiskTriggerMetaData.getName()).ifPresent(executableWhiskTrigger ->
                        EventUtils.publish(e.getProject(),
                                OpenAndFireTriggerControlActionListener.TOPIC,
                                (l) -> l.openAndFireTriggerControlWindow(whiskAuth, executableWhiskTrigger)));
            } catch (IOException ex) {
                final String msg = "The trigger cannot be loaded: " + whiskTriggerMetaData.getName();
                LOG.error(msg, ex);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
            }
        }
    }
}
