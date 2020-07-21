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

package org.apache.openwhisk.intellij.connector.dialog.action.ui;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.openwhisk.intellij.connector.dialog.action.listener.DownActionListener;
import org.apache.openwhisk.intellij.connector.dialog.action.listener.RemoveActionListener;
import org.apache.openwhisk.intellij.connector.dialog.action.listener.UpActionListener;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.intellij.icons.AllIcons.General.*;

public class LinkedActionsEntryForm {
    private final static Logger LOG = Logger.getInstance(LinkedActionsEntryForm.class);

    private JPanel mainJPanel;
    private JTextField actionJTextField;
    private JButton upJButton;
    private JButton downJButton;
    private JButton removeJButton;
    private JLabel indexJLabel;

    private String namespace;

    public LinkedActionsEntryForm(int index, String namespace, String actionName, UpActionListener upActionListener, DownActionListener downActionListener, RemoveActionListener removeActionListener) {
        this.namespace = namespace;

        indexJLabel.setText(String.valueOf(index));
        indexJLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setActionName(actionName);

        upJButton.setIcon(ArrowUp);
        upJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                upActionListener.upAction(Integer.parseInt(indexJLabel.getText()));
            }
        });

        downJButton.setIcon(ArrowDown);
        downJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                downActionListener.downAction(Integer.parseInt(indexJLabel.getText()));
            }
        });

        removeJButton.setIcon(Remove);
        removeJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                removeActionListener.removeAction(Integer.parseInt(indexJLabel.getText()));
            }
        });
    }

    private void setActionName(String actionName) {
        String name = actionName.replaceAll("/" + namespace + "/", "");
        actionJTextField.setText(name);
    }

    public String getActionName() {
        return "/" + namespace + "/" + actionJTextField.getText();
    }

    public void setIndex(int index) {
        indexJLabel.setText(String.valueOf(index));
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}
