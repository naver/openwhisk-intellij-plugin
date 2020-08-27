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

package com.navercorp.openwhisk.intellij.explorer.dialog.trigger.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.utils.ParameterUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.SimplifiedWhiskRule;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskRuleService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import com.navercorp.openwhisk.intellij.run.toolwindow.listener.RefreshActionOrTriggerListener;
import org.apache.commons.lang.RandomStringUtils;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;

import static com.intellij.icons.AllIcons.General.Add;

public class TriggerManagerDialogForm {
    private static final Logger LOG = Logger.getInstance(TriggerManagerDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private static final String DEFAULT_RULE_TEXTAREA_MSG = "If not entered, it is automatically generated.";

    private JPanel mainJPanel;
    private JPanel tirggerNameJPanel;
    private JPanel triggerActionsJPanel;
    private JLabel triggerNameJLabel;

    private JComboBox selectActionJComboBox;
    private JTextField ruleNameJTextField;
    private JButton addJButton;
    private JScrollPane linkedActionsJScrollPane;
    private JPanel linkedActionsJPanel;
    private JPanel triggerDefaultParameterJPanel;
    private JTextArea defaultParameterJTextArea;

    private Project project;
    private Map<String, LinkedActionsForm> cachedRules = new HashMap<>();
    private Map<String, LinkedActionsForm> removedRules = new HashMap<>();
    private ExecutableWhiskTrigger cachedTrigger;
    private WhiskAuth auth;
    private WhiskTriggerService whiskTriggerService;
    private WhiskRuleService whiskRuleService;

    public TriggerManagerDialogForm(Project project, WhiskAuth auth, ExecutableWhiskTrigger trigger, List<WhiskActionMetaData> actions) {
        this.project = project;
        this.auth = auth;
        this.cachedTrigger = trigger;
        this.whiskTriggerService = WhiskTriggerService.getInstance();
        this.whiskRuleService = WhiskRuleService.getInstance();

        triggerNameJLabel.setText(trigger.getName());

        try {
            defaultParameterJTextArea.setText(JsonParserUtils.writeParameterToJson(trigger.getParameters()));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse json: " + trigger.getName(), e);
        }

        selectActionJComboBox.setModel(new ComboBoxModel() {
            private WhiskActionMetaData selected;

            @Override
            public void setSelectedItem(Object anItem) {
                selected = (WhiskActionMetaData) anItem;
            }

            @Override
            public Object getSelectedItem() {
                return selected;
            }

            @Override
            public int getSize() {
                return actions.size();
            }

            @Override
            public Object getElementAt(int index) {
                return actions.get(index);
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

        ruleNameJTextField.setText(DEFAULT_RULE_TEXTAREA_MSG);

        String namespace = trigger.getNamespace();
        for (Map.Entry<String, SimplifiedWhiskRule> set : trigger.getRules().entrySet()) {
            String ruleName = set.getKey().replace(namespace + "/", "");
            String actionName = set.getValue().getAction().getPkgActionName();
            addLinkedAction(actionName, ruleName);
        }

        addJButton.setIcon(Add);
        addJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectActionJComboBox.getSelectedItem() == null) {
                    return;
                }

                String ruleName = getRuleName(ruleNameJTextField.getText());
                if (existRule(ruleName)) {
                    NOTIFIER.notify(project, "The " + ruleName + " already exist", NotificationType.WARNING);
                    return;
                }

                WhiskActionMetaData action = (WhiskActionMetaData) selectActionJComboBox.getSelectedItem();
                String actionName = action.getWhiskPackage().map(pkg -> pkg + "/" + action.getName()).orElse(action.getName());
                addLinkedAction(actionName, ruleName);
            }
        });
    }

    public void updateTrigger() {
        try {
            /**
             * Update default parameters
             */
            Optional<String> params = ParameterUtils.validateParams(defaultParameterJTextArea.getText());
            if (!params.isPresent()) {
                NOTIFIER.notify(project, "The json format of the parameter is incorrect.", NotificationType.ERROR);
                return;
            }
            // parameters
            List<Map<String, Object>> validParams = ParameterUtils.mapToListMap(JsonParserUtils.parseMap(params.get()));
            // payload
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("name", cachedTrigger.getName());
            payload.put("parameters", validParams);
            whiskTriggerService.updateWhiskTrigger(auth, cachedTrigger.getName(), payload);

            /**
             * Update rules
             */
            for (LinkedActionsForm linkedActionsForm : cachedRules.values()) {
                String ruleName = linkedActionsForm.getRule();
                String actionName = linkedActionsForm.getAction();
                // payload
                Map<String, Object> rulePayload = new LinkedHashMap<>();
                rulePayload.put("name", ruleName);
                rulePayload.put("trigger", "/_/" + cachedTrigger.getName());
                rulePayload.put("action", "/_/" + actionName);
                rulePayload.put("status", "");
                whiskRuleService.updateWhiskRule(auth, ruleName, rulePayload).ifPresent(rule ->
                        LOG.info("Updated rule: " + rule.getName()));
            }

            /**
             * Remove rules
             */
            for (LinkedActionsForm linkedActionsForm : removedRules.values()) {
                whiskRuleService.deleteWhiskRule(auth, linkedActionsForm.getRule()).ifPresent(rule ->
                        LOG.info("Deleted rule: " + rule.getName()));
            }

            NOTIFIER.notify(project, cachedTrigger.getName() + " updated", NotificationType.INFORMATION);
            EventUtils.publish(project, RefreshActionOrTriggerListener.TOPIC, RefreshActionOrTriggerListener::fetchActionMetadata);
        } catch (IOException e) {
            String msg = "Failed to update trigger: " + cachedTrigger.getName();
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }

    /**
     * Helper functions.
     */
    private void addLinkedAction(String actionName, String ruleName) {
        LinkedActionsForm linkedActionsForm = new LinkedActionsForm(project, actionName, ruleName, this::removeLinkedAction);
        linkedActionsJPanel.setLayout(new BoxLayout(linkedActionsJPanel, BoxLayout.Y_AXIS));
        linkedActionsJPanel.add(linkedActionsForm.getContent());
        cachedRules.put(ruleName, linkedActionsForm);
        linkedActionsJPanel.updateUI();
    }

    private void removeLinkedAction(String ruleName) {
        LinkedActionsForm form = cachedRules.get(ruleName);
        removedRules.put(ruleName, form);
        cachedRules.remove(ruleName);
        linkedActionsJPanel.remove(form.getContent());
        linkedActionsJPanel.updateUI();
    }

    private boolean existRule(String ruleName) {
        for (String r : cachedRules.keySet()) {
            if (r.equals(ruleName.trim())) {
                return true;
            }
        }
        return false;
    }

    private String getRuleName(String userInput) {
        if (DEFAULT_RULE_TEXTAREA_MSG.trim().equals(userInput.trim())) {
            // auto generate
            return generateName();
        } else {
            // use user input
            return userInput.trim();
        }
    }

    private String generateName() {
        return RandomStringUtils.randomAlphabetic(30);
    }

    public JPanel getContent() {
        return mainJPanel;
    }
}
