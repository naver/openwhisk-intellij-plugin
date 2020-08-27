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
import com.navercorp.openwhisk.intellij.run.toolwindow.listener.RefreshActionOrTriggerListener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Nullable
    private DefaultParameterForm defaultParameterForm;

    @Nullable
    private DockerImageForm dockerImageForm;

    @Nullable
    private LinkedActionsForm linkedActionsForm;

    private Project project;
    private WhiskAuth whiskAuth;
    private ExecutableWhiskAction action;

    private WhiskActionService whiskActionService = WhiskActionService.getInstance();

    public ActionManagerDialogForm(Project project, WhiskAuth auth, ExecutableWhiskAction action, List<WhiskActionMetaData> actions) {
        this.project = project;
        this.whiskAuth = auth;
        this.action = action;

        switch (action.getKind()) {
            case "sequence":
                // add linked actions panel
                linkedActionsForm = new LinkedActionsForm(project, action.getNamespace().split("/")[0], actions, action.getExec().getComponents());
                linkedActionsJPanel.add(linkedActionsForm.getContent(), BorderLayout.CENTER);

                // remove docker image panel
                mainJPanel.remove(dockerImageJPanel);
                mainJPanel.remove(dockerImageJSeparator);

                // remove default parameter panel
                mainJPanel.remove(defaultParameterJPanel);
                mainJPanel.remove(defaultParameterJSeparator);
                break;
            case "blackbox":
                // remove linked actions panel
                mainJPanel.remove(linkedActionsJPanel);
                mainJPanel.remove(linkedActionsJSeparator);

                // add docker image panel
                dockerImageForm = new DockerImageForm();
                dockerImageJPanel.add(dockerImageForm.getContent(), BorderLayout.CENTER);

                // add default parameter panel
                defaultParameterForm = new DefaultParameterForm();
                defaultParameterJPanel.add(defaultParameterForm.getContent(), BorderLayout.CENTER);
                break;
            default: // normal
                // remove linked actions panel
                mainJPanel.remove(linkedActionsJPanel);
                mainJPanel.remove(linkedActionsJSeparator);

                // remove docker image panel
                mainJPanel.remove(dockerImageJPanel);
                mainJPanel.remove(dockerImageJSeparator);

                // add default parameter panel
                defaultParameterForm = new DefaultParameterForm();
                defaultParameterJPanel.add(defaultParameterForm.getContent(), BorderLayout.CENTER);
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
                switch (index) {
                    case 0:
                        return Runtime.NODE_6;
                    case 1:
                        return Runtime.NODE_8;
                    case 2:
                        return Runtime.NODE_10;
                    case 3:
                        return Runtime.NODE_12;
                    case 4:
                        return Runtime.PYTHON_2;
                    case 5:
                        return Runtime.PYTHON_3;
                    case 6:
                        return Runtime.JAVA;
                    case 7:
                        return Runtime.SWIFT_3_1_1;
                    case 8:
                        return Runtime.SWIFT_4_2;
                    case 9:
                        return Runtime.PHP_7_2;
                    case 10:
                        return Runtime.GO_1_11;
                    case 11:
                        return Runtime.RUBY_2_5;
                    case 12:
                        return Runtime.SEQUENCE;
                    default:
                        return Runtime.DOCKER;
                }
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

        if (defaultParameterForm != null) {
            try {
                // default parameter
                defaultParameterForm.setDefaultParameter(JsonParserUtils.writeParameterToJson(action.getParameters()));
            } catch (IOException e) {
                LOG.error("Failed to parse json: " + action.getFullyQualifiedName(), e);
            }
        }

        if (dockerImageForm != null) {
            dockerImageForm.setDockerImage(action.getExec().getImage());
        }
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
            // annotations
            List<Map<String, Object>> annotations = annotationToCollection(action, web, rawHttp, customOption, finalDefaultParameter);

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
            });
        } catch (IOException e) {
            String msg = "Failed to update action: " + action.getFullyQualifiedName();
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }

    private Optional<String> getParameter() {
        if (defaultParameterForm == null) {
            return Optional.of("{}");
        }
        return ParameterUtils.validateParams(defaultParameterForm.getDefaultParameter());
    }

    private CodeExec createExec(ExecutableWhiskAction action, Runtime runtime) {
        CodeExec codeExec = action.getExec();
        codeExec.setKind(runtime.toString());

        if (dockerImageForm != null) {
            codeExec.setImage(dockerImageForm.getDockerImage());
        }

        if (linkedActionsForm != null) {
            codeExec.setComponents(linkedActionsForm.getComponents());
        }

        return codeExec;
    }

    private List<Map<String, Object>> annotationToCollection(ExecutableWhiskAction action,
                                                             boolean web,
                                                             boolean rawHttp,
                                                             boolean customOption,
                                                             boolean finalDefaultParameter) {
        Map<String, Object> annotations = ParameterUtils.listMapToMap(action.getAnnotations());
        annotations.put("web-export", web);
        annotations.put("raw-http", rawHttp);
        annotations.put("web-custom-options", customOption);
        annotations.put("final", finalDefaultParameter);
        return ParameterUtils.mapToListMap(annotations);
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}
