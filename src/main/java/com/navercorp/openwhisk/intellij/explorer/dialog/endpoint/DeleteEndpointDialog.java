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

package com.navercorp.openwhisk.intellij.explorer.dialog.endpoint;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class DeleteEndpointDialog extends DialogWrapper {

    private WhiskEndpoint whiskEndpoint;

    public DeleteEndpointDialog(Project project, WhiskEndpoint whiskEndpoint) {
        super(true); // use current window as parent
        setTitle("Delete Endpoint");
        setResizable(false);
        this.whiskEndpoint = whiskEndpoint;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Are you sure you want to delete " + whiskEndpoint.getAlias() + " endpoint?");
        label.setPreferredSize(new Dimension(100, 30));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}
