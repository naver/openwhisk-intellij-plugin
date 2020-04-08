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

package org.apache.openwhisk.intellij.connector.dialog.trigger;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.apache.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class DeleteTriggerDialog extends DialogWrapper {

    private WhiskTriggerMetaData whiskTriggerMetaData;

    public DeleteTriggerDialog(Project project, WhiskTriggerMetaData whiskTriggerMetaData) {
        super(true); // use current window as parent
        setTitle("Delete Trigger");
        setResizable(false);
        this.whiskTriggerMetaData = whiskTriggerMetaData;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Are you sure you want to delete " + whiskTriggerMetaData.getName() + " trigger?");
        label.setPreferredSize(new Dimension(100, 30));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}
