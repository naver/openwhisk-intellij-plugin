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

package com.navercorp.openwhisk.intellij.explorer.dialog.trigger;

import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.DialogWrapperWithApply;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.explorer.dialog.trigger.ui.TriggerCreationManagerDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TriggerCreationManagerDialog extends DialogWrapperWithApply {

    private TriggerCreationManagerDialogForm triggerCreationManagerDialogForm;

    public TriggerCreationManagerDialog(Project project, WhiskAuth auth) {
        super(project, true); // use current window as parent
        triggerCreationManagerDialogForm = new TriggerCreationManagerDialogForm(project, auth);
        setTitle("Create Trigger");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (triggerCreationManagerDialogForm != null) {
            return triggerCreationManagerDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (triggerCreationManagerDialogForm != null) {
            triggerCreationManagerDialogForm.createTrigger();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (triggerCreationManagerDialogForm != null && !myApplyAction.isApplied()) {
            triggerCreationManagerDialogForm.createTrigger();
        }
        super.doOKAction();
    }
}
