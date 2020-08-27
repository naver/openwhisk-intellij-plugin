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

package com.navercorp.openwhisk.intellij.run.toolwindow.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.utils.ParameterUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationWithLogs;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActivationService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

public class WhiskRunWindowForm {
    private static final Logger LOG = Logger.getInstance(WhiskRunWindowForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    public static final String ENTITY_ACTION = "Action";
    public static final String ENTITY_TRIGGER = "Trigger";

    /**
     * Main Panel.
     */
    private JPanel mainJPanel;

    private JSplitPane executionJSplitPane;
    // Parameter
    private JPanel parameterJPanel;
    private JPanel executionActionsJPanel;
    private JTextArea paramJTextArea;
    private JTextArea resultJTextArea;
    private JScrollPane resultJScrollPane;

    /**
     * Cache data.
     */
    private ToolWindow toolWindow;
    private Project project;
    private Optional<ExecutableWhiskAction> currentAction = Optional.empty();
    private Optional<ExecutableWhiskTrigger> currentTrigger = Optional.empty();
    private Optional<WhiskAuth> currentAuth = Optional.empty();
    private String currentEntity = "";

    public WhiskRunWindowForm(Project project, ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.project = project;

        final Color defaultBackgroundColor = EditorColorsManager.getInstance().getSchemeForCurrentUITheme().getDefaultBackground();

        /**
         * Action Tab
         */
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionActionGroup = (ActionGroup) actionManager.getAction("WhiskRunWindow.Actions.Controls");
        ActionToolbar actionActionToolbar = actionManager.createActionToolbar("", actionActionGroup, true);
        actionActionToolbar.setTargetComponent(executionActionsJPanel);
        executionActionsJPanel.add(actionActionToolbar.getComponent());

        resultJTextArea.setText("Please run action");
        resultJTextArea.setBackground(defaultBackgroundColor);
        executionJSplitPane.setBackground(defaultBackgroundColor);
    }

    public JPanel getContent() {
        return mainJPanel;
    }

    public void updateResult(String result) {
        resultJTextArea.setText(result);
    }

    public void cacheAction(ExecutableWhiskAction action) {
        this.currentAction = Optional.ofNullable(action);
    }

    public void cacheTrigger(ExecutableWhiskTrigger trigger) {
        this.currentTrigger = Optional.ofNullable(trigger);
    }

    public void cacheAuth(WhiskAuth auth) {
        this.currentAuth = Optional.ofNullable(auth);
    }

    public Optional<ExecutableWhiskAction> getCachedAction() {
        return this.currentAction;
    }

    public Optional<ExecutableWhiskTrigger> getCachedTrigger() {
        return this.currentTrigger;
    }

    public Optional<WhiskAuth> getCachedAuth() {
        return this.currentAuth;
    }

    public void setTitle(String title) {
        toolWindow.setTitle(title);
    }

    public String getCurrentEntity() {
        return currentEntity;
    }

    /**
     * Action Tab.
     */
    public void initializeActionTab(ExecutableWhiskAction action) {
        try {
            paramJTextArea.setText(JsonParserUtils.writeParameterToJson(action.getParameters()));
            resultJTextArea.setText("Please run action");
            currentEntity = ENTITY_ACTION;
        } catch (IOException e) {
            LOG.error("Failed to parse json: " + action.getFullyQualifiedName(), e);
        }
    }

    public void runAction(WhiskActionService whiskActionService, WhiskAuth auth, ExecutableWhiskAction action) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Run action") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    if (StringUtils.isEmpty(paramJTextArea.getText())) {
                        paramJTextArea.setText("{}");
                    }

                    Optional<String> params = ParameterUtils.validateParams(paramJTextArea.getText());
                    if (params.isPresent()) {
                        String result = whiskActionService.invokeWhiskAction(auth,
                                Optional.ofNullable(action.getNamespacePath()),
                                action.getWhiskPackage(),
                                action.getName(),
                                params.get());
                        updateResult(result);
                    } else {
                        NOTIFIER.notify(project, "The json format of the parameter is incorrect.", NotificationType.ERROR);
                    }
                } catch (IOException e) {
                    LOG.error("Failed to invoke action: " + action.getFullyQualifiedName(), e);
                }

            }
        });
    }

    public void refreshActionMetaData(WhiskActionService whiskActionService, WhiskAuth auth, ExecutableWhiskAction action) {
        try {
            whiskActionService.getWhiskAction(auth, Optional.ofNullable(action.getNamespacePath()), action.getWhiskPackage(), action.getName())
                    .ifPresent(executableWhiskAction -> {
                        cacheAction(executableWhiskAction);
                        initializeActionTab(executableWhiskAction);
                        NOTIFIER.notify(project, "Refreshed action metadata: " + executableWhiskAction.getFullyQualifiedName(), NotificationType.INFORMATION);
                    });
        } catch (IOException e) {
            LOG.error("Failed to fetch action: " + action.getFullyQualifiedName(), e);
        }
    }

    /**
     * Trigger Tab.
     */
    public void initializeTriggerTab(ExecutableWhiskTrigger trigger) {
        try {
            paramJTextArea.setText(JsonParserUtils.writeParameterToJson(trigger.getParameters()));
            resultJTextArea.setText("Please fire trigger");
            currentEntity = ENTITY_TRIGGER;
        } catch (IOException e) {
            LOG.error("Failed to parse json: " + trigger.getName(), e);
        }
    }

    public void fireTrigger(WhiskTriggerService whiskTriggerService,
                            WhiskActivationService whiskActivationService,
                            WhiskAuth auth,
                            ExecutableWhiskTrigger trigger) {
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Fire trigger") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    if (StringUtils.isEmpty(paramJTextArea.getText())) {
                        paramJTextArea.setText("{}");
                    }

                    Optional<String> params = ParameterUtils.validateParams(paramJTextArea.getText());
                    if (params.isPresent()) {
                        Optional<String> result = whiskTriggerService.fireWhiskTrigger(auth, trigger.getName(), params.get());
                        if (result.isPresent()) {
                            String activationId = (String) JsonParserUtils.parseMap(result.get()).get("activationId");
                            getActivation(whiskActivationService, auth, activationId, 10)
                                    .ifPresent(response -> updateResult(response));
                        } else {
                            updateResult("Trigger was fired (no return value)");
                        }

                    } else {
                        NOTIFIER.notify(project, "The json format of the parameter is incorrect.", NotificationType.ERROR);
                    }
                } catch (IOException e) {
                    LOG.error("Failed to fire trigger: " + trigger.getName(), e);
                }
            }
        });
    }

    private Optional<String> getActivation(WhiskActivationService whiskActivationService, WhiskAuth auth, String activationId, int retryCount) {
        if (retryCount == 0) {
            LOG.error("Failed to get activation " + activationId);
            return Optional.empty();
        }
        try {
            return whiskActivationService.getWhiskActivation(auth, activationId).flatMap(this::getResponse);
        } catch (IOException e) {
            LOG.info("Get activation " + activationId + ", retry count: " + retryCount);
            waitThread(500);
            return getActivation(whiskActivationService, auth, activationId, retryCount - 1);
        }
    }

    private Optional<String> getResponse(WhiskActivationWithLogs activation) {
        try {
            return Optional.ofNullable(JsonParserUtils.beautifyJson(JsonParserUtils.writeMapToJson(activation.getResponse())));
        } catch (IOException e) {
            LOG.error("Failed to parse response", e);
            return Optional.empty();
        }
    }

    private void waitThread(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    public void refreshTrigger(WhiskAuth auth, WhiskTriggerService whiskTriggerService, ExecutableWhiskTrigger old) {
        try {
            whiskTriggerService.getWhiskTrigger(auth, old.getName()).ifPresent(newTrigger -> {
                cacheTrigger(newTrigger);
                initializeTriggerTab(newTrigger);
            });
        } catch (IOException e) {
            final String msg = "The action cannot be executed.";
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }
}
