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

package org.apache.openwhisk.intellij.wskdeploy.navigation.action;

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
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ResourceUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.openwhisk.intellij.common.utils.EventUtils;
import org.apache.openwhisk.intellij.wskdeploy.dialog.WskDeployTempleteDialog;
import org.apache.openwhisk.intellij.wskdeploy.navigation.listener.RefreshWskDeployManifestListener;
import org.apache.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;

public class CreateManifestTemplateAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(CreateManifestTemplateAction.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    CreateManifestTemplateAction() {
        super(AllIcons.Actions.Menu_paste);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final String basePath = project.getBasePath();

        // find manifest file in project
        PsiFile[] YAMLFiles = FilenameIndex.getFilesByName(project, "manifest.yaml", GlobalSearchScope.allScope(project));
        PsiFile[] YMLFiles = FilenameIndex.getFilesByName(project, "manifest.yml", GlobalSearchScope.allScope(project));
        PsiFile[] manifestFiles = ArrayUtils.addAll(YAMLFiles, YMLFiles);
        // find hello.js file in project
        PsiFile[] jsFiles = FilenameIndex.getFilesByName(project, "hello.js", GlobalSearchScope.allScope(project));


        if ((new WskDeployTempleteDialog(project, manifestFiles, jsFiles)).showAndGet()) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    if (manifestFiles.length == 0) {
                        // manifest template file
                        VirtualFile manifestFile = VfsUtil.findFileByURL(ResourceUtil.getResource(getClass(), "/template/", "manifest.yaml"));
                        final VirtualFile baseParent = VfsUtil.findFile(Paths.get(basePath), true);
                        VfsUtilCore.copyFile(this, manifestFile, baseParent);
                        NOTIFIER.notify(project, manifestFile.getName() + " is created", NotificationType.INFORMATION);
                    }

                    if (jsFiles.length == 0) {
                        // action template file
                        VirtualFile jsFile = VfsUtil.findFileByURL(ResourceUtil.getResource(getClass(), "/template/src/", "hello.js"));
                        final VirtualFile jsParent = VfsUtil.createDirectories(basePath + "/src");
                        VfsUtilCore.copyFile(this, jsFile, jsParent);
                        NOTIFIER.notify(project, jsFile.getName() + " is created", NotificationType.INFORMATION);
                    }

                    // refresh manifest file tree
                    EventUtils.publish(project, RefreshWskDeployManifestListener.TOPIC, RefreshWskDeployManifestListener::refreshWskDeployManifest);
                } catch (IOException e1) {
                    LOG.error(e1);
                }
            });
        }
    }
}
