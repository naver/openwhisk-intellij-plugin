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

package org.apache.openwhisk.intellij.connector.navigation.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.openwhisk.intellij.common.notification.SimpleNotifier;
import org.apache.openwhisk.intellij.connector.dialog.endpoint.AddEndpointDialog;
import org.jetbrains.annotations.NotNull;

import static com.intellij.icons.AllIcons.General.Add;

public class AddEndpointAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(AddEndpointAction.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    public AddEndpointAction() {
        super("Add Endpoint", "Add Endpoint", Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AddEndpointDialog dialog = new AddEndpointDialog(e.getProject());
        if (dialog.showAndGet()) {
            LOG.info("AddEndpointDialogForm is closed");
        }
    }
}
