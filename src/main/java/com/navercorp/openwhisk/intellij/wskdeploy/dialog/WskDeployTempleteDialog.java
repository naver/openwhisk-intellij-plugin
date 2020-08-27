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

package com.navercorp.openwhisk.intellij.wskdeploy.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class WskDeployTempleteDialog extends DialogWrapper {

    private Project project;
    private String[] metaFiles;
    private String[] srcFiles;

    public WskDeployTempleteDialog(Project project, String[] metaFiles, String[] srcFiles) {
        super(true); // use current window as parent
        setTitle("Create WskDeploy Template");
        setResizable(false);
        this.project = project;
        this.metaFiles = metaFiles;
        this.srcFiles = srcFiles;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("Are you sure you want to create wskdeploy project in your workspace?<br/>");
        builder.append("The following file will be created:<br/>");
        for (String path : metaFiles) {
            builder.append("- " + path + "<br/>");
        }

        for (String path : srcFiles) {
            builder.append("- src/" + path + "<br/>");
        }
        builder.append("</html>");
        JLabel label = new JLabel(builder.toString());
        label.setPreferredSize(new Dimension(500, 100));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}
