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
import com.navercorp.openwhisk.intellij.explorer.dialog.endpoint.ui.AddEndpointDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AddEndpointDialog extends DialogWrapperWithApply {

    private AddEndpointDialogForm addEndpointDialogForm;

    public AddEndpointDialog(Project project) {
        super(project, true); // use current window as parent
        addEndpointDialogForm = new AddEndpointDialogForm(project);
        setTitle("Add Endpoint");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (addEndpointDialogForm != null) {
            return addEndpointDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (addEndpointDialogForm != null) {
            addEndpointDialogForm.addEndpoint();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (addEndpointDialogForm != null && !myApplyAction.isApplied()) {
            addEndpointDialogForm.addEndpoint();
        }
        super.doOKAction();
    }
}
