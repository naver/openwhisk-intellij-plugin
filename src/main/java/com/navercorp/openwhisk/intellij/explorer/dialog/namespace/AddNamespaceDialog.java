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
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.explorer.dialog.namespace.ui.AddNamespaceDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AddNamespaceDialog extends DialogWrapperWithApply {

    private AddNamespaceDialogForm addNamespaceDialogForm;

    public AddNamespaceDialog(Project project, WhiskEndpoint whiskEndpoint) {
        super(project, true); // use current window as parent
        addNamespaceDialogForm = new AddNamespaceDialogForm(project, whiskEndpoint);
        setTitle("Add Namespace");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (addNamespaceDialogForm != null) {
            return addNamespaceDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (addNamespaceDialogForm != null) {
            addNamespaceDialogForm.addNamespace();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (addNamespaceDialogForm != null && !myApplyAction.isApplied()) {
            addNamespaceDialogForm.addNamespace();
        }
        super.doOKAction();
    }
}
