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

package com.navercorp.openwhisk.intellij.run.toolwindow.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.run.toolwindow.listener.OpenActionManagerListener;
import org.jetbrains.annotations.NotNull;

import static com.intellij.icons.AllIcons.Actions.Edit;

public class OpenActionManagerDialogAction extends AnAction {

    public OpenActionManagerDialogAction() {
        super(Edit);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        EventUtils.publish(e.getProject(), OpenActionManagerListener.TOPIC, OpenActionManagerListener::openActionManagerDialog);
    }
}
