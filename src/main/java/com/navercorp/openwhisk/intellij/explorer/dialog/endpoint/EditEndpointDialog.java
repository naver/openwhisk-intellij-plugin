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
import com.navercorp.openwhisk.intellij.common.DialogWrapperWithApply;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.explorer.dialog.endpoint.ui.EditEndpointDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EditEndpointDialog extends DialogWrapperWithApply {

    private EditEndpointDialogForm editEndpointDialogForm;

    public EditEndpointDialog(Project project, WhiskEndpoint whiskEndpoint) {
        super(project, true); // use current window as parent
        editEndpointDialogForm = new EditEndpointDialogForm(project, whiskEndpoint);
        setTitle("Edit Endpoint");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (editEndpointDialogForm != null) {
            return this.editEndpointDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (editEndpointDialogForm != null) {
            editEndpointDialogForm.updateEndpoint();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (editEndpointDialogForm != null && !myApplyAction.isApplied()) {
            editEndpointDialogForm.updateEndpoint();
        }
        super.doOKAction();
    }
}
