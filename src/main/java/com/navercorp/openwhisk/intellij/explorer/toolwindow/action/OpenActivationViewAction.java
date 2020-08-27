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

package com.navercorp.openwhisk.intellij.explorer.toolwindow.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.explorer.editor.ActivationViewVirtualFile;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.intellij.icons.AllIcons.Nodes.Module;

public class OpenActivationViewAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(OpenActivationViewAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private List<WhiskEndpoint> endpoints;

    private Optional<WhiskAuth> whiskAuth;
    private Optional<WhiskActionMetaData> action;
    private Optional<WhiskTriggerMetaData> trigger;

    public OpenActivationViewAction(List<WhiskEndpoint> endpoints,
                                    Optional<WhiskAuth> whiskAuth,
                                    Optional<WhiskActionMetaData> action,
                                    Optional<WhiskTriggerMetaData> trigger) {
        super("Activation", "Open activation view", Module);
        this.endpoints = endpoints;
        this.whiskAuth = whiskAuth;
        this.action = action;
        this.trigger = trigger;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile virtualFile = new ActivationViewVirtualFile(project, endpoints, whiskAuth, action, trigger);
        fileEditorManager.openFile(virtualFile, true);
    }
}
