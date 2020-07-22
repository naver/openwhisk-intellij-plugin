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

import com.google.common.collect.Streams;
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
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.wskdeploy.dialog.WskDeployTempleteDialog;
import com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.listener.RefreshWskDeployManifestListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateManifestTemplateAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(CreateManifestTemplateAction.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();
    private final String[] FILTER_PATH = new String[]{".idea"};

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
        List<PsiFile> manifestFiles = Streams.concat(Arrays.stream(YAMLFiles), Arrays.stream(YMLFiles))
                .filter(file -> filterByPath(file, FILTER_PATH))
                .collect(Collectors.toList());

        // find hello.js file in project
        PsiFile[] jsFiles = FilenameIndex.getFilesByName(project, "hello.js", GlobalSearchScope.allScope(project));
        List<PsiFile> sampleFiles = Arrays.stream(jsFiles)
                .filter(file -> filterByPath(file, FILTER_PATH))
                .collect(Collectors.toList());

        if ((new WskDeployTempleteDialog(project, manifestFiles, sampleFiles)).showAndGet()) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    if (manifestFiles.size() == 0) {
                        // manifest template file
                        VirtualFile manifestFile = VfsUtil.findFileByURL(ResourceUtil.getResource(getClass(), "/template/", "manifest.yaml"));
                        final VirtualFile baseParent = VfsUtil.findFileByURL(Paths.get(basePath).toUri().toURL());

                        VfsUtilCore.copyFile(this, manifestFile, baseParent);
                        NOTIFIER.notify(project, manifestFile.getName() + " is created", NotificationType.INFORMATION);
                    }

                    if (sampleFiles.size() == 0) {
                        // action template file
                        VirtualFile sampleFile = VfsUtil.findFileByURL(ResourceUtil.getResource(getClass(), "/template/src/", "hello.js"));
                        final VirtualFile sampleParent = VfsUtil.createDirectories(basePath + "/src");
                        VfsUtilCore.copyFile(this, sampleFile, sampleParent);
                        NOTIFIER.notify(project, sampleFile.getName() + " is created", NotificationType.INFORMATION);
                    }

                    // refresh manifest file tree
                    EventUtils.publish(project, RefreshWskDeployManifestListener.TOPIC, RefreshWskDeployManifestListener::refreshWskDeployManifest);
                } catch (IOException e1) {
                    LOG.error(e1);
                }
            });
        }
    }

    private boolean filterByPath(PsiFile file, String[] filterList) {
        for (String path : filterList) {
            if (file.getVirtualFile().getPath().contains(path)) {
                return false;
            }
        }
        return true;
    }
}
