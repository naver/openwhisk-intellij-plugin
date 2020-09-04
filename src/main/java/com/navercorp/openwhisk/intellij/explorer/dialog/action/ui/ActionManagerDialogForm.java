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

package com.navercorp.openwhisk.intellij.explorer.dialog.action.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.utils.ParameterUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.Limits;
import com.navercorp.openwhisk.intellij.common.whisk.model.Runtime;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.CodeExec;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.run.toolwindow.listener.RefreshActionOrTriggerListener;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.intellij.uiDesigner.core.GridConstraints.*;

public class ActionManagerDialogForm {
    private static final Logger LOG = Logger.getInstance(ActionManagerDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JComboBox runtimeJComboBox;
    private JCheckBox webactionJCheckBox;
    private JCheckBox rawHttpJCheckBox;
    private JCheckBox customOptionHeaderJCheckBox;
    private JCheckBox finalOptionJCheckBox;
    private JSpinner timeoutJSpinner;
    private JSpinner memoryJSpinner;
    private JPanel defaultParameterJPanel;
    private JSeparator defaultParameterJSeparator;
    private JPanel dockerImageJPanel;
    private JSeparator dockerImageJSeparator;
    private JPanel linkedActionsJPanel;
    private JSeparator linkedActionsJSeparator;
    private JSeparator codeTypeJSeparator;
    private JPanel codeTypeJPanel;

    private DefaultParameterForm defaultParameterForm;
    private DockerImageForm dockerImageForm;
    private CodeTypeForm codeTypeForm;
    private LinkedActionsForm linkedActionsForm;

    private Project project;
    private WhiskAuth whiskAuth;
    private ExecutableWhiskAction action;

    private WhiskActionService whiskActionService = WhiskActionService.getInstance();

    public ActionManagerDialogForm(Project project, WhiskAuth auth, ExecutableWhiskAction action, List<WhiskActionMetaData> actions) {
        this.project = project;
        this.whiskAuth = auth;
        this.action = action;

        // add linked actions panel
        linkedActionsForm = new LinkedActionsForm(project, action.getNamespace().split("/")[0], actions, action.getExec().getComponents());
        linkedActionsJPanel.add(linkedActionsForm.getContent(), BorderLayout.CENTER);

        // add docker image panel
        dockerImageForm = new DockerImageForm();
        dockerImageJPanel.add(dockerImageForm.getContent(), BorderLayout.CENTER);

        // add code type panel
        codeTypeForm = new CodeTypeForm();
        codeTypeJPanel.add(codeTypeForm.getContent(), BorderLayout.CENTER);

        // add default parameter panel
        defaultParameterForm = new DefaultParameterForm();
        defaultParameterJPanel.add(defaultParameterForm.getContent(), BorderLayout.CENTER);

        switch (action.getKind()) {
            case "sequence":
                // remove docker image panel
                mainJPanel.remove(dockerImageJPanel);
                mainJPanel.remove(dockerImageJSeparator);

                // remove code type panel
                mainJPanel.remove(codeTypeJPanel);
                mainJPanel.remove(codeTypeJSeparator);

                // remove default parameter panel
                mainJPanel.remove(defaultParameterJPanel);
                mainJPanel.remove(defaultParameterJSeparator);
                break;
            case "blackbox":
                // remove linked actions panel
                mainJPanel.remove(linkedActionsJPanel);
                mainJPanel.remove(linkedActionsJSeparator);
                break;
            default: // normal
                // remove linked actions panel
                mainJPanel.remove(linkedActionsJPanel);
                mainJPanel.remove(linkedActionsJSeparator);

                // remove docker image panel
                mainJPanel.remove(dockerImageJPanel);
                mainJPanel.remove(dockerImageJSeparator);

                // remove code type panel
                mainJPanel.remove(codeTypeJPanel);
                mainJPanel.remove(codeTypeJSeparator);
        }

        runtimeJComboBox.setModel(new ComboBoxModel<Runtime>() {
            private Runtime selected;

            @Override
            public void setSelectedItem(Object anItem) {
                selected = (Runtime) anItem;
            }

            @Override
            public Runtime getSelectedItem() {
                return this.selected;
            }

            @Override
            public int getSize() {
                return Runtime.values().length;
            }

            @Override
            public Runtime getElementAt(int index) {
                return Runtime.toRuntime(index);
            }

            @Override
            public void addListDataListener(ListDataListener l) {
                // nothing to do
            }

            @Override
            public void removeListDataListener(ListDataListener l) {
                // nothing to do
            }
        });

        if (!webactionJCheckBox.isSelected()) {
            // raw http
            rawHttpJCheckBox.setEnabled(false);
            // custom option header
            customOptionHeaderJCheckBox.setEnabled(false);
            // final option
            finalOptionJCheckBox.setEnabled(false);
        }

        webactionJCheckBox.addChangeListener(e -> {
            AbstractButton abstractButton = (AbstractButton) e.getSource();
            ButtonModel buttonModel = abstractButton.getModel();

            if (buttonModel.isSelected()) {
                // raw http
                rawHttpJCheckBox.setEnabled(true);
                // custom option header
                customOptionHeaderJCheckBox.setEnabled(true);
                // final option
                finalOptionJCheckBox.setEnabled(true);
            } else {
                // raw http
                rawHttpJCheckBox.setSelected(false);
                rawHttpJCheckBox.setEnabled(false);
                // custom option header
                customOptionHeaderJCheckBox.setSelected(false);
                customOptionHeaderJCheckBox.setEnabled(false);
                // final option
                finalOptionJCheckBox.setSelected(false);
                finalOptionJCheckBox.setEnabled(false);
            }
        });

        /**
         * Set Value
         */
        // runtime
        runtimeJComboBox.setSelectedItem(Runtime.toRuntime(action.getKind()));
        // timeout
        timeoutJSpinner.setValue(action.getLimits().getTimeout());
        // memory
        memoryJSpinner.setValue(action.getLimits().getMemory());
        // webaction
        webactionJCheckBox.setSelected(action.isWebAction());
        // raw http
        rawHttpJCheckBox.setSelected(action.isRawHttp());
        // custom option header
        customOptionHeaderJCheckBox.setSelected(action.isCustomOptions());
        // final option
        finalOptionJCheckBox.setSelected(action.isFinalDefaultParameter());

        try {
            // default parameter
            defaultParameterForm.setDefaultParameter(JsonParserUtils.writeParameterToJson(action.getParameters()));
        } catch (IOException e) {
            LOG.error("Failed to parse json: " + action.getFullyQualifiedName(), e);
        }
        // docker image url
        dockerImageForm.setDockerImage(action.getExec().getImage());
        // code type
        action.getCodeType().ifPresent(codeType -> codeTypeForm.setCodeType(codeType));

        /**
         * Set runtime event
         */
        runtimeJComboBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            switch ((Runtime) e.getItem()) {
                case SEQUENCE:
                    removeAllPanel();

                    mainJPanel.add(linkedActionsJPanel, new GridConstraints(7, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, 150), new Dimension(-1, -1), 0));
                    mainJPanel.add(linkedActionsJSeparator, new GridConstraints(8, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, 5), 0));

                    mainJPanel.updateUI();
                    break;
                case DOCKER:
                    removeAllPanel();

                    mainJPanel.add(dockerImageJPanel, new GridConstraints(7, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, -1), 0));
                    mainJPanel.add(dockerImageJSeparator, new GridConstraints(8, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, 5), 0));

                    mainJPanel.add(codeTypeJPanel, new GridConstraints(9, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, -1), 0));
                    mainJPanel.add(codeTypeJSeparator, new GridConstraints(10, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, 5), 0));

                    mainJPanel.add(defaultParameterJPanel, new GridConstraints(11, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, -1), 0));
                    mainJPanel.add(defaultParameterJSeparator, new GridConstraints(12, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, 5), 0));

                    mainJPanel.updateUI();
                    break;
                default: // normal
                    removeAllPanel();

                    mainJPanel.add(defaultParameterJPanel, new GridConstraints(7, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, -1), 0));
                    mainJPanel.add(defaultParameterJSeparator, new GridConstraints(8, 0, 1, 1,
                            SIZEPOLICY_FIXED, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER,
                            new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, 5), 0));

                    mainJPanel.updateUI();
            }
        });
    }

    private void removeAllPanel() {
        // remove linked actions panel
        mainJPanel.remove(linkedActionsJPanel);
        mainJPanel.remove(linkedActionsJSeparator);

        // remove docker image panel
        mainJPanel.remove(dockerImageJPanel);
        mainJPanel.remove(dockerImageJSeparator);

        // remove code type panel
        mainJPanel.remove(codeTypeJPanel);
        mainJPanel.remove(codeTypeJSeparator);

        // remove default parameter panel
        mainJPanel.remove(defaultParameterJPanel);
        mainJPanel.remove(defaultParameterJSeparator);
    }

    public void updateAction() {
        try {
            // parameters
            Optional<String> params = getParameter();
            if (!params.isPresent()) {
                NOTIFIER.notify(project, "The json format of the parameter is incorrect.", NotificationType.ERROR);
                return;
            }
            List<Map<String, Object>> validParams = ParameterUtils.mapToListMap(JsonParserUtils.parseMap(params.get()));
            // timeout
            int timeout = (Integer) timeoutJSpinner.getValue();
            // memory
            int memory = (Integer) memoryJSpinner.getValue();
            Limits limits = action.getLimits();
            limits.setTimeout(timeout);
            limits.setMemory(memory);
            // runtime
            Runtime runtime = (Runtime) runtimeJComboBox.getSelectedItem();
            CodeExec exec = createExec(action, runtime);
            // web action
            boolean web = webactionJCheckBox.isSelected();
            boolean rawHttp = rawHttpJCheckBox.isSelected();
            boolean customOption = customOptionHeaderJCheckBox.isSelected();
            boolean finalDefaultParameter = finalOptionJCheckBox.isSelected();
            // code type
            Optional<String> codeType = getCodeType(runtime);
            // annotations
            List<Map<String, Object>> annotations = annotationToCollection(action, web, rawHttp, customOption, finalDefaultParameter, codeType);

            // payload
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("namespace", action.getNamespacePath());
            payload.put("name", action.getWhiskPackage().map(pkg -> pkg + "/" + action.getName()).orElse(action.getName()));
            payload.put("parameters", validParams);
            payload.put("annotations", annotations);
            payload.put("limits", limits);
            payload.put("exec", exec);
            whiskActionService.updateWhiskAction(whiskAuth, action, payload).ifPresent(updated -> {
                NOTIFIER.notify(project, "Action update succeeded: " + updated.getFullyQualifiedName(), NotificationType.INFORMATION);
                EventUtils.publish(project, RefreshActionOrTriggerListener.TOPIC, RefreshActionOrTriggerListener::fetchActionMetadata);
                EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
            });
        } catch (IOException e) {
            String msg = "Failed to update action: " + action.getFullyQualifiedName();
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }

    private Optional<String> getCodeType(Runtime runtime) {
        switch (runtime) {
            case DOCKER:
                Runtime codeType = codeTypeForm.getSelectedCodeType();
                return Optional.ofNullable(codeType.toString());
            default:
                return Optional.empty();
        }
    }

    private Optional<String> getParameter() {
        if (defaultParameterForm == null) {
            return Optional.of("{}");
        }
        return ParameterUtils.validateParams(defaultParameterForm.getDefaultParameter());
    }

    private CodeExec createExec(ExecutableWhiskAction executableWhiskAction, Runtime runtime) {
        CodeExec codeExec = executableWhiskAction.getExec();
        codeExec.setKind(runtime.toString());

        switch (runtime) {
            case SEQUENCE:
                codeExec.setComponents(linkedActionsForm.getComponents());
                break;
            case DOCKER:
                codeExec.setImage(dockerImageForm.getDockerImage());
                break;
            default: // normal
        }

        return codeExec;
    }

    private List<Map<String, Object>> annotationToCollection(ExecutableWhiskAction executableWhiskAction,
                                                             boolean web,
                                                             boolean rawHttp,
                                                             boolean customOption,
                                                             boolean finalDefaultParameter,
                                                             Optional<String> codeType) {
        Map<String, Object> annotations = ParameterUtils.listMapToMap(executableWhiskAction.getAnnotations());
        annotations.put("web-export", web);
        annotations.put("raw-http", rawHttp);
        annotations.put("web-custom-options", customOption);
        annotations.put("final", finalDefaultParameter);
        codeType.map(ct -> annotations.put("code-type", ct));
        return ParameterUtils.mapToListMap(annotations);
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}
