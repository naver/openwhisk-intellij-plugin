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
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import com.navercorp.openwhisk.intellij.explorer.dialog.pkg.ui.PackageManagerDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PackageManagerDialog extends DialogWrapperWithApply {

    private PackageManagerDialogForm packageManagerDialogForm;

    public PackageManagerDialog(Project project, WhiskAuth auth, WhiskPackageWithActions whiskPackage) {
        super(project, true); // use current window as parent
        packageManagerDialogForm = new PackageManagerDialogForm(project, auth, whiskPackage);
        setTitle("Manage Package");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (packageManagerDialogForm != null) {
            return this.packageManagerDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (packageManagerDialogForm != null) {
            packageManagerDialogForm.updatePackage();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (packageManagerDialogForm != null && !myApplyAction.isApplied()) {
            packageManagerDialogForm.updatePackage();
        }
        super.doOKAction();
    }
}
