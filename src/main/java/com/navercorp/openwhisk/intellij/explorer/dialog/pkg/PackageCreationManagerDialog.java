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
import com.navercorp.openwhisk.intellij.common.DialogWrapperWithApply;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.explorer.dialog.pkg.ui.PackageCreationManagerDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PackageCreationManagerDialog extends DialogWrapperWithApply {

    private PackageCreationManagerDialogForm packageCreationManagerDialogForm;

    public PackageCreationManagerDialog(Project project, WhiskAuth auth) {
        super(project, true); // use current window as parent
        packageCreationManagerDialogForm = new PackageCreationManagerDialogForm(project, auth);
        setTitle("Create Package");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (packageCreationManagerDialogForm != null) {
            return packageCreationManagerDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (packageCreationManagerDialogForm != null) {
            packageCreationManagerDialogForm.createPackage();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (packageCreationManagerDialogForm != null && !myApplyAction.isApplied()) {
            packageCreationManagerDialogForm.createPackage();
        }
        super.doOKAction();
    }
}
