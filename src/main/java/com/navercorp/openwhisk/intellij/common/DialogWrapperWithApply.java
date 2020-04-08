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

package com.navercorp.openwhisk.intellij.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class DialogWrapperWithApply extends DialogWrapper {

    protected ApplyAction myApplyAction;

    protected DialogWrapperWithApply(@Nullable Project project, boolean canBeParent) {
        super(project, canBeParent);
        myApplyAction = new DialogWrapperWithApply.ApplyAction("Apply");
    }

    /**
     * This method is invoked by default implementation of "Apply" action.
     * This is convenient place to override functionality of "Apply" action.
     */
    protected void doApplyAction() {
        // do noting
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{
                myCancelAction, myOKAction, myApplyAction
        };
    }

    protected class ApplyAction extends DialogWrapperAction {

        private boolean isApplied;

        protected ApplyAction(@NotNull String name) {
            super(name);
            isApplied = false;
        }

        @Override
        protected void doAction(ActionEvent e) {
            doApplyAction();
            isApplied = true;
        }

        public boolean isApplied() {
            return isApplied;
        }
    }
}
