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

package org.apache.openwhisk.intellij.explorer.toolwindow.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.openwhisk.intellij.common.notification.SimpleNotifier;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import org.apache.openwhisk.intellij.explorer.dialog.namespace.AddNamespaceDialog;
import org.jetbrains.annotations.NotNull;

public class AddNamespaceAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(AddNamespaceAction.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskEndpoint whiskEndpoint;

    public AddNamespaceAction(WhiskEndpoint whiskEndpoint) {
        super("Add Namespace", "Add Namespace", AllIcons.General.Add);
        this.whiskEndpoint = whiskEndpoint;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskEndpoint != null) {
            AddNamespaceDialog dialog = new AddNamespaceDialog(e.getProject(), whiskEndpoint);
            if (dialog.showAndGet()) {
                LOG.info("AddNamespaceDialog is closed");
            }
        }
    }
}