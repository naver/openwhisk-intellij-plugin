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
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.explorer.dialog.trigger.ui.TriggerManagerDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class TriggerManagerDialog extends DialogWrapperWithApply {

    private TriggerManagerDialogForm triggerManagerDialogForm;

    public TriggerManagerDialog(Project project, WhiskAuth auth, ExecutableWhiskTrigger trigger, List<WhiskActionMetaData> actions) {
        super(project, true); // use current window as parent
        triggerManagerDialogForm = new TriggerManagerDialogForm(project, auth, trigger, actions);
        setTitle("Manage Trigger");
        setResizable(false);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (triggerManagerDialogForm != null) {
            return this.triggerManagerDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (triggerManagerDialogForm != null) {
            triggerManagerDialogForm.updateTrigger();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (triggerManagerDialogForm != null && !myApplyAction.isApplied()) {
            triggerManagerDialogForm.updateTrigger();
        }
        super.doOKAction();
    }
}
