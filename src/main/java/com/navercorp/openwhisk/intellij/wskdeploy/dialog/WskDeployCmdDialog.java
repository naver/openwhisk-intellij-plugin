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

package com.navercorp.openwhisk.intellij.wskdeploy.dialog;

import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.DialogWrapperWithApply;
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.WskDeployCmd;
import com.navercorp.openwhisk.intellij.wskdeploy.dialog.ui.WskDeployCmdDialogForm;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class WskDeployCmdDialog extends DialogWrapperWithApply {


    private WskDeployCmdDialogForm wskDeployCmdDialogForm;
    private WskDeployCmd cmd;

    public WskDeployCmdDialog(Project project, WskDeployCmd cmd) {
        super(project, true); // use current window as parent
        setTitle("Run WskDeploy Command");
        setResizable(false);
        this.cmd = cmd;
        wskDeployCmdDialogForm = new WskDeployCmdDialogForm(project, cmd);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        if (wskDeployCmdDialogForm != null) {
            return this.wskDeployCmdDialogForm.getContent();
        } else {
            return null;
        }
    }

    @Override
    protected void doApplyAction() {
        if (wskDeployCmdDialogForm != null) {
            wskDeployCmdDialogForm.runWskDeploy();
        }
        super.doApplyAction();
    }

    @Override
    protected void doOKAction() {
        if (wskDeployCmdDialogForm != null && !myApplyAction.isApplied()) {
            wskDeployCmdDialogForm.runWskDeploy();
        }
        super.doOKAction();
    }
}
