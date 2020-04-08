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

package com.navercorp.openwhisk.intellij.explorer.dialog.namespace;

import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.DialogWrapperWithApply;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import com.navercorp.openwhisk.intellij.explorer.dialog.namespace.ui.EditNamespaceDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EditNamespaceDialog extends DialogWrapperWithApply {

    private EditNamespaceDialogForm editNamespaceDialogForm;

    public EditNamespaceDialog(Project project, WhiskAuth whiskAuth, WhiskNamespace whiskNamespace) {
        super(project, true); // use current window as parent
        editNamespaceDialogForm = new EditNamespaceDialogForm(project, whiskAuth, whiskNamespace);
        setTitle("Edit Namespace");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (editNamespaceDialogForm != null) {
            return this.editNamespaceDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (editNamespaceDialogForm != null) {
            editNamespaceDialogForm.updateNamespace();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (editNamespaceDialogForm != null && !myApplyAction.isApplied()) {
            editNamespaceDialogForm.updateNamespace();
        }
        super.doOKAction();
    }
}
