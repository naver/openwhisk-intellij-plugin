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

package com.navercorp.openwhisk.intellij.explorer.dialog.action;

import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.DialogWrapperWithApply;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.explorer.dialog.action.ui.ActionManagerDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class ActionManagerDialog extends DialogWrapperWithApply {

    private ActionManagerDialogForm actionManagerDialogForm;

    public ActionManagerDialog(Project project, WhiskAuth auth, ExecutableWhiskAction action, List<WhiskActionMetaData> actions) {
        super(project, true); // use current window as parent
        actionManagerDialogForm = new ActionManagerDialogForm(project, auth, action, actions);
        setTitle("Manage Action");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (actionManagerDialogForm != null) {
            return this.actionManagerDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (actionManagerDialogForm != null) {
            actionManagerDialogForm.updateAction();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (actionManagerDialogForm != null && !myApplyAction.isApplied()) {
            actionManagerDialogForm.updateAction();
        }
        super.doOKAction();
    }
}
