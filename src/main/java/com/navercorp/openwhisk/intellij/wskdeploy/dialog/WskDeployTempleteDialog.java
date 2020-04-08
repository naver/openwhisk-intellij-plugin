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
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class WskDeployTempleteDialog extends DialogWrapper {

    private Project project;
    private PsiFile[] manifests;
    private PsiFile[] sampleactions;

    public WskDeployTempleteDialog(Project project, PsiFile[] manifests, PsiFile[] sampleactions) {
        super(true); // use current window as parent
        setTitle("Create WskDeploy Template");
        setResizable(false);
        this.project = project;
        this.manifests = manifests;
        this.sampleactions = sampleactions;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        final String basePath = project.getBasePath();
        JPanel dialogPanel = new JPanel(new BorderLayout());

        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("Are you sure you want to create wskdeploy project in your workspace?<br/>");
        builder.append("The following file will be created:<br/>");
        builder.append("- manifest.yaml<br/>");
        builder.append("- src/hello.js<br/>");
        builder.append("<br/><br/>");

        if (manifests.length > 0 | sampleactions.length > 0) {
            builder.append("Files that already exist in the path below are not created:<br/>");
        }
        if (manifests.length > 0) {
            for (PsiFile m : manifests) {
                builder.append("- " + m.getVirtualFile().getPath().replaceAll(basePath + "/", "") + "<br/>");
            }
        }
        if (sampleactions.length > 0) {
            for (PsiFile s : sampleactions) {
                builder.append("- " + s.getVirtualFile().getPath().replaceAll(basePath + "/", "") + "<br/>");
            }
        }

        builder.append("</html>");
        JLabel label = new JLabel(builder.toString());
        label.setMinimumSize(new Dimension(200, 100));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}
