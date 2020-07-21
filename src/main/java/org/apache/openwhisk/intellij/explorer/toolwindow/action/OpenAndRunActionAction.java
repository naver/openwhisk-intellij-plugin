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

package org.apache.openwhisk.intellij.explorer.toolwindow.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.openwhisk.intellij.common.utils.EventUtils;
import org.apache.openwhisk.intellij.run.toolwindow.listener.OpenAndRunActionControlActionListener;
import org.apache.openwhisk.intellij.common.notification.SimpleNotifier;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskAuth;
import org.apache.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import org.apache.openwhisk.intellij.common.whisk.service.WhiskActionService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

import static com.intellij.icons.AllIcons.Actions.Execute;

public class OpenAndRunActionAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(OpenAndRunActionAction.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskActionService whiskActionService = WhiskActionService.getInstance();

    private WhiskActionMetaData whiskActionMetaData;
    private WhiskAuth whiskAuth;

    public OpenAndRunActionAction(WhiskAuth whiskAuth, WhiskActionMetaData whiskActionMetaData) {
        super("Run", "Run action", Execute);
        this.whiskActionMetaData = whiskActionMetaData;
        this.whiskAuth = whiskAuth;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null && whiskActionMetaData != null) {
            try {
                whiskActionService.getWhiskAction(whiskAuth, Optional.ofNullable(whiskActionMetaData.getNamespacePath()), whiskActionMetaData.getWhiskPackage(), whiskActionMetaData.getName())
                        .ifPresent(executableWhiskAction ->
                                EventUtils.publish(e.getProject(), OpenAndRunActionControlActionListener.TOPIC, (l) -> l.openAndRunActionControlWindow(whiskAuth, executableWhiskAction)));
            } catch (IOException ex) {
                final String msg = "The action cannot be loaded: " + whiskActionMetaData.getFullyQualifiedName();
                LOG.error(msg, ex);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
            }

        }

    }
}
