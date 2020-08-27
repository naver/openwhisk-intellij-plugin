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
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.icons.AllIcons.General.Add;

public class LinkedActionsForm {
    private static final Logger LOG = Logger.getInstance(LinkedActionsForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JComboBox actionsJComboBox;
    private JButton addJButton;
    private JPanel linkedActionsJPanel;

    private Project project;
    private List<LinkedActionsEntryForm> cachedActions = new ArrayList<>();

    public LinkedActionsForm(Project project, String namespace, List<WhiskActionMetaData> actions, List<String> components) {
        this.project = project;

        for (int i = 0; i < components.size(); i++) {
            addLinkedAction(i, namespace, components.get(i));
        }

        actionsJComboBox.setModel(new ComboBoxModel() {
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

        addJButton.setIcon(Add);
        addJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionsJComboBox.getSelectedItem() == null) {
                    return;
                }

                WhiskActionMetaData action = (WhiskActionMetaData) actionsJComboBox.getSelectedItem();
                String actionName = action.getWhiskPackage().map(pkg -> pkg + "/" + action.getName()).orElse(action.getName());
                addLinkedAction(cachedActions.size(), namespace, actionName);
            }
        });
    }

    public List<String> getComponents() {
        return cachedActions.stream().map(LinkedActionsEntryForm::getActionName).collect(Collectors.toList());
    }

    public JPanel getContent() {
        return mainJPanel;
    }

    /**
     * Helper functions.
     */
    private void addLinkedAction(int index, String namespace, String actionName) {
        LinkedActionsEntryForm entry = new LinkedActionsEntryForm(index, namespace, actionName,
                this::upLinkedAction,
                this::downLinkedAction,
                this::removeLinkedAction);
        linkedActionsJPanel.setLayout(new BoxLayout(linkedActionsJPanel, BoxLayout.Y_AXIS));
        linkedActionsJPanel.add(entry.getContent());
        cachedActions.add(entry);
        linkedActionsJPanel.updateUI();
    }

    private void upLinkedAction(int index) {
        if (index <= 0) {
            return;
        }

        for (int i = index - 1; i < cachedActions.size(); i++) {
            linkedActionsJPanel.remove(cachedActions.get(i).getContent());
        }

        Collections.swap(cachedActions, index - 1, index);

        for (int i = index - 1; i < cachedActions.size(); i++) {
            LinkedActionsEntryForm l = cachedActions.get(i);
            l.setIndex(i);
            linkedActionsJPanel.add(l.getContent());
        }

        linkedActionsJPanel.updateUI();
    }

    private void downLinkedAction(int index) {
        if (index >= cachedActions.size() - 1) {
            return;
        }

        for (int i = index; i < cachedActions.size(); i++) {
            linkedActionsJPanel.remove(cachedActions.get(i).getContent());
        }

        Collections.swap(cachedActions, index, index + 1);

        for (int i = index; i < cachedActions.size(); i++) {
            LinkedActionsEntryForm l = cachedActions.get(i);
            l.setIndex(i);
            linkedActionsJPanel.add(l.getContent());
        }

        linkedActionsJPanel.updateUI();
    }

    private void removeLinkedAction(int index) {
        if (cachedActions.size() <= 1) {
            NOTIFIER.notify(project, "Sequence actions require at least one action.", NotificationType.WARNING);
            return;
        }

        linkedActionsJPanel.remove(cachedActions.get(index).getContent());
        cachedActions.remove(index);
        for (int i = index; i < cachedActions.size(); i++) {
            cachedActions.get(i).setIndex(i);
        }

        linkedActionsJPanel.updateUI();
    }
}
