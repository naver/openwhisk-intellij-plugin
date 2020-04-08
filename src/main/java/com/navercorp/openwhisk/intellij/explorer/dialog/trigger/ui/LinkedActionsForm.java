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

import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.explorer.dialog.trigger.listener.RemoveLinkedRuleListener;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.intellij.icons.AllIcons.General.Remove;

public class LinkedActionsForm {
    private JTextField actionJTextField;
    private JTextField ruleJTextField;
    private JButton deleteJButton;
    private JPanel mainJPanel;

    public LinkedActionsForm(Project project, String actionName, String ruleName, RemoveLinkedRuleListener remover) {
        actionJTextField.setText(actionName);
        actionJTextField.setEnabled(false);
        ruleJTextField.setText(ruleName);
        ruleJTextField.setEnabled(false);

        deleteJButton.setIcon(Remove);
        deleteJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String ruleName = ruleJTextField.getText();
                remover.removeLinkedRule(ruleName);
            }
        });
    }


    public String getAction() {
        return actionJTextField.getText();
    }

    public String getRule() {
        return ruleJTextField.getText();
    }

    public JPanel getContent() {
        return mainJPanel;
    }
}
