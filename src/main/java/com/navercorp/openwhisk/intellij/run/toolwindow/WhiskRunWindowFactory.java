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

package com.navercorp.openwhisk.intellij.run.toolwindow;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActivationService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import com.navercorp.openwhisk.intellij.run.toolwindow.listener.*;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.explorer.dialog.action.ActionManagerDialog;
import com.navercorp.openwhisk.intellij.explorer.dialog.trigger.TriggerManagerDialog;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.OpenActionControlActionListener;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.OpenTriggerControlActionListener;
import com.navercorp.openwhisk.intellij.run.toolwindow.ui.WhiskRunWindowForm;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static com.intellij.icons.AllIcons.Debugger.Console;
import static com.navercorp.openwhisk.intellij.run.toolwindow.ui.WhiskRunWindowForm.ENTITY_ACTION;
import static com.navercorp.openwhisk.intellij.run.toolwindow.ui.WhiskRunWindowForm.ENTITY_TRIGGER;

public class WhiskRunWindowFactory implements ToolWindowFactory {
    private static final Logger LOG = Logger.getInstance(WhiskRunWindowFactory.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private static final String ID = "Whisk Run";

    private WhiskActionService whiskActionService = WhiskActionService.getInstance();
    private WhiskActivationService whiskActivationService = WhiskActivationService.getInstance();
    private WhiskTriggerService whiskTriggerService = WhiskTriggerService.getInstance();

    private WhiskRunWindowForm whiskRunWindowForm;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.whiskRunWindowForm = new WhiskRunWindowForm(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(whiskRunWindowForm.getContent(), null, false);
        toolWindow.getContentManager().addContent(content);

        toolWindow.setIcon(Console);
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.getToolWindow(ID).hide(() -> {
        });

        /**
         * Receive Action events
         */
        EventUtils.subscribe(project, project, OpenActionControlActionListener.TOPIC,
                (auth, executableWhiskAction) ->
                        toolWindowManager.getToolWindow(ID).show(() ->
                                initializeWhiskTabWithAction(auth, executableWhiskAction, false)));

        EventUtils.subscribe(project, project, OpenAndRunActionControlActionListener.TOPIC,
                (auth, executableWhiskAction) ->
                        toolWindowManager.getToolWindow(ID).show(() ->
                                initializeWhiskTabWithAction(auth, executableWhiskAction, true)));

        /**
         * Receive Trigger events
         */
        EventUtils.subscribe(project, project, OpenTriggerControlActionListener.TOPIC, (auth, executableWhiskTrigger) ->
                toolWindowManager.getToolWindow(ID).show(() ->
                        initializeWhiskTabWithTrigger(auth, executableWhiskTrigger, false)));

        EventUtils.subscribe(project, project, OpenAndFireTriggerControlActionListener.TOPIC, (auth, executableWhiskTrigger) ->
                toolWindowManager.getToolWindow(ID).show(() ->
                        initializeWhiskTabWithTrigger(auth, executableWhiskTrigger, true)));

        /**
         * Receive Action Toolbar events
         */
        EventUtils.subscribe(project, project, RunActionListener.TOPIC, () ->
                whiskRunWindowForm.getCachedAuth().ifPresent(auth -> {
                    String entity = whiskRunWindowForm.getCurrentEntity();
                    if (entity.equals(ENTITY_ACTION)) {
                        whiskRunWindowForm.getCachedAction().ifPresent(action ->
                                whiskRunWindowForm.runAction(whiskActionService, auth, action));
                    } else if (entity.equals(ENTITY_TRIGGER)) {
                        whiskRunWindowForm.getCachedTrigger().ifPresent(trigger ->
                                whiskRunWindowForm.fireTrigger(whiskTriggerService, whiskActivationService, auth, trigger));
                    }
                }));

        EventUtils.subscribe(project, project, RefreshActionOrTriggerListener.TOPIC, () ->
                whiskRunWindowForm.getCachedAuth().ifPresent(auth -> {
                    String entity = whiskRunWindowForm.getCurrentEntity();
                    if (entity.equals(ENTITY_ACTION)) {
                        whiskRunWindowForm.getCachedAction().ifPresent(action ->
                                whiskRunWindowForm.refreshActionMetaData(whiskActionService, auth, action));
                    } else if (entity.equals(ENTITY_TRIGGER)) {
                        whiskRunWindowForm.getCachedTrigger().ifPresent(trigger ->
                                whiskRunWindowForm.refreshTrigger(auth, whiskTriggerService, trigger));
                    }
                }));

        EventUtils.subscribe(project, project, OpenActionManagerListener.TOPIC, () ->
                whiskRunWindowForm.getCachedAuth().ifPresent(auth -> {
                    try {
                        List<WhiskActionMetaData> actions = whiskActionService.getWhiskActions(auth);
                        String entity = whiskRunWindowForm.getCurrentEntity();
                        if (entity.equals(ENTITY_ACTION)) {
                            whiskRunWindowForm.getCachedAction().ifPresent(action -> {
                                ActionManagerDialog dialog = new ActionManagerDialog(project, auth, action, actions);
                                if (dialog.showAndGet()) {
                                    LOG.info("ActionManagerDialog is closed");
                                }
                            });
                        } else if (entity.equals(ENTITY_TRIGGER)) {
                            whiskRunWindowForm.getCachedTrigger().ifPresent(trigger -> {
                                TriggerManagerDialog dialog = new TriggerManagerDialog(project, auth, trigger, actions);
                                if (dialog.showAndGet()) {
                                    LOG.info("TriggerManagerDialog is closed");
                                }
                            });
                        }
                    } catch (IOException e) {
                        final String msg = "The action cannot be executed.";
                        LOG.error(msg, e);
                        NOTIFIER.notify(project, msg, NotificationType.ERROR);
                    }
                }));
    }

    private void initializeWhiskTabWithAction(WhiskAuth auth, ExecutableWhiskAction executableWhiskAction, boolean isRunAction) {
        whiskRunWindowForm.cacheAction(executableWhiskAction);
        whiskRunWindowForm.cacheAuth(auth);
        whiskRunWindowForm.setTitle(executableWhiskAction.getFullyQualifiedName());
        whiskRunWindowForm.initializeActionTab(executableWhiskAction);
        if (isRunAction) {
            whiskRunWindowForm.runAction(whiskActionService, auth, executableWhiskAction);
        }
    }

    private void initializeWhiskTabWithTrigger(WhiskAuth auth, ExecutableWhiskTrigger executableWhiskTrigger, boolean isFireTrigger) {
        whiskRunWindowForm.cacheAuth(auth);
        whiskRunWindowForm.cacheTrigger(executableWhiskTrigger);
        whiskRunWindowForm.setTitle(executableWhiskTrigger.getName());
        whiskRunWindowForm.initializeTriggerTab(executableWhiskTrigger);
        if (isFireTrigger) {
            whiskRunWindowForm.fireTrigger(whiskTriggerService, whiskActivationService, auth, executableWhiskTrigger);
        }
    }
}
