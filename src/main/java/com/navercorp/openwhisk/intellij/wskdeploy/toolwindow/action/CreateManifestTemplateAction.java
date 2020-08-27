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

package com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.action;

import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ResourceUtil;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.wskdeploy.dialog.WskDeployTempleteDialog;
import com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.listener.RefreshWskDeployManifestListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

public class CreateManifestTemplateAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(CreateManifestTemplateAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private static final String[] META_FILES = new String[]{"manifest.yaml", "HOW-TO-DEPLOY.md"};
    private static final String[] SOURCE_FILES = new String[]{"index.js", "index.test.js", "package.json"};

    CreateManifestTemplateAction() {
        super(AllIcons.Actions.Menu_paste);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final String basePath = project.getBasePath();

        if ((new WskDeployTempleteDialog(project, META_FILES, SOURCE_FILES)).showAndGet()) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                for (String filePath : META_FILES) {
                    copyFile(project, "/template", filePath, basePath);
                }

                for (String filePath : SOURCE_FILES) {
                    copyFile(project, "/template/src", filePath, basePath + "/src");
                }

                // refresh manifest file tree
                EventUtils.publish(project, RefreshWskDeployManifestListener.TOPIC, RefreshWskDeployManifestListener::refreshWskDeployManifest);
            });
        }
    }

    private void copyFile(Project project, String resourcePath, String fileName, String toDir) {
        try {
            boolean exist = Arrays.stream(FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.allScope(project)))
                    .anyMatch(file -> file.getVirtualFile().getPath().startsWith(toDir));

            if (!exist) {
                final VirtualFile file = VfsUtil.findFileByURL(ResourceUtil.getResource(getClass(), resourcePath, fileName));
                final VirtualFile parent = VfsUtil.createDirectories(toDir);
                VfsUtilCore.copyFile(this, file, parent);
                NOTIFIER.notify(project, fileName + " file is created", NotificationType.INFORMATION);
            } else {
                NOTIFIER.notify(project, fileName + " file already exist", NotificationType.WARNING);
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
