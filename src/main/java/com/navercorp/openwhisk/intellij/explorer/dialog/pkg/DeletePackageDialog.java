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

package com.navercorp.openwhisk.intellij.explorer.dialog.pkg;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class DeletePackageDialog extends DialogWrapper {

    private WhiskPackageWithActions whiskPackage;

    public DeletePackageDialog(Project project, WhiskPackageWithActions whiskPackage) {
        super(true); // use current window as parent
        setTitle("Delete Package");
        setResizable(false);
        this.whiskPackage = whiskPackage;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("Are you sure you want to delete " + whiskPackage.getName() + " package?");
        if (!whiskPackage.getBinding().isPresent()) {
            if (!whiskPackage.getActions().isEmpty()) {
                builder.append("<br/><br/>");
                builder.append("The following actions will be deleted together<br/>");
            }
            for (CompactWhiskAction action : whiskPackage.getActions()) {
                builder.append("- " + action.getName() + "<br/>");
            }
            builder.append("<br/><br/>");
        }
        builder.append("</html>");

        JLabel label = new JLabel(builder.toString());
        label.setPreferredSize(new Dimension(400, 100));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}
