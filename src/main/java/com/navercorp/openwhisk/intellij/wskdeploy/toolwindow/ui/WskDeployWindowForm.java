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

package com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.service.WskDeployService;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.ValidationUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.*;
import com.navercorp.openwhisk.intellij.wskdeploy.dialog.WskDeployCmdDialog;
import com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.listener.ChooseWskDeployBinaryListener;
import com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.listener.RefreshWskDeployManifestListener;
import com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.tree.WskDeployTreeCellRenderer;
import com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.tree.WskDeployTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WskDeployWindowForm {
    private static final Logger LOG = Logger.getInstance(WskDeployWindowForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private static final String WSKDEPLOY_URL = "https://github.com/apache/openwhisk-wskdeploy/releases";

    private JPanel mainJPanel;
    private JPanel actionsJPanel;
    private JScrollPane contentsJPanel;
    private JTree wskdeployJTree;
    private JPanel downloadGuideJPanel;
    private JLabel urlJLabel;

    private WskDeployService wskDeployService;

    public WskDeployWindowForm(Project project, ToolWindow toolWindow) {
        this.wskDeployService = ServiceManager.getService(project, WskDeployService.class);

        // Toolbar
        String actionGroupName = "WskDeployWindow.Actions.Controls";
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction(actionGroupName);
        ActionToolbar actionToolbar = actionManager.createActionToolbar("", actionGroup, true);
        actionToolbar.setTargetComponent(actionsJPanel);
        actionsJPanel.add(actionToolbar.getComponent());

        // Download url for wskdeploy
        urlJLabel.setText("<html> WskDeploy download : <a href=\\\"\\\">" + WSKDEPLOY_URL + "</a></html>");
        urlJLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        urlJLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(WSKDEPLOY_URL));
                } catch (IOException | URISyntaxException ex) {
                    String msg = "Cannot open " + WSKDEPLOY_URL;
                    LOG.error(msg, ex);
                    NOTIFIER.notify(project, msg, NotificationType.ERROR);
                }
            }
        });

        setWskDeployTree(project, loadWskDeployManifest(project));
    }

    private void setWskDeployTree(Project project, List<WskDeployManifest> manifests) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows"); // TODO make a global value
        WskDeployFile wskDeployFile = loadRegisteredWskDeploy()  // 1. Registered wskdeploy
                .orElse(loadWskDeployFileFromLocal(isWindows));  // 2. Local wskdeploy

        wskdeployJTree.setModel(new WskDeployTreeModel(wskDeployFile, manifests));
        wskdeployJTree.setCellRenderer(new WskDeployTreeCellRenderer());
        expandAllNode(wskdeployJTree);
        wskdeployJTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) wskdeployJTree.getLastSelectedPathComponent();
                    Object userObject = node.getUserObject();
                    if (userObject instanceof WskDeployCmdDeploy) {
                        WskDeployCmdDeploy cmd = (WskDeployCmdDeploy) userObject;
                        if (new WskDeployCmdDialog(project, cmd).showAndGet()) {
                            LOG.info("WskDeployCmdDialog(deploy) closed");
                        }
                    } else if (userObject instanceof WskDeployCmdUndeploy) {
                        WskDeployCmdUndeploy cmd = (WskDeployCmdUndeploy) userObject;
                        if (new WskDeployCmdDialog(project, cmd).showAndGet()) {
                            LOG.info("WskDeployCmdDialog(undeploy) closed");
                        }
                    }
                }
            }
        });

        EventUtils.subscribe(project, project, RefreshWskDeployManifestListener.TOPIC, () -> {
            WskDeployFile reloadedWskDeployFile = loadRegisteredWskDeploy()  // 1. Registered wskdeploy
                    .orElse(loadWskDeployFileFromLocal(isWindows));          // 2. Local wskdeploy

            wskdeployJTree.setModel(new WskDeployTreeModel(reloadedWskDeployFile, loadWskDeployManifest(project)));
            expandAllNode(wskdeployJTree);
        });

        EventUtils.subscribe(project, project, ChooseWskDeployBinaryListener.TOPIC, (chosenWskDeployFile) -> {
            WskDeployBinary wskDeployBinary = new WskDeployBinary(chosenWskDeployFile.getPath(), chosenWskDeployFile.getName());
            wskDeployService.setWskdeployName(wskDeployBinary.getName());
            wskDeployService.setWskdeployPath(wskDeployBinary.getFullPath());
            wskDeployService.loadState(wskDeployService);

            wskdeployJTree.setModel(new WskDeployTreeModel(wskDeployBinary, loadWskDeployManifest(project)));
            expandAllNode(wskdeployJTree);
        });
    }


    private Optional<WskDeployFile> loadRegisteredWskDeploy() {
        if (wskDeployService.getWskdeployPath() != null && wskDeployService.getWskdeployName() != null) {
            VirtualFile file = LocalFileSystem.getInstance().findFileByPath(wskDeployService.getWskdeployPath());
            return ValidationUtils.validateWskDeploy(Optional.ofNullable(file))
                    .map(validWskDeploy -> new WskDeployBinary(validWskDeploy.getPath(), validWskDeploy.getName()));
        } else {
            return Optional.empty();
        }
    }

    private WskDeployFile loadWskDeployFileFromLocal(boolean isWindow) {
        String[] dirs;
        if (isWindow) {
            dirs = new String[]{};
        } else {
            dirs = new String[]{"/usr/local/bin", "/usr/bin"};
        }

        for (String dir : dirs) {
            File[] files = findFiles(dir, "wskdeploy");
            if (files != null && files.length > 0) {
                VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(files[0]);
                Optional<WskDeployBinary> wskDeployBinary = ValidationUtils.validateWskDeploy(Optional.ofNullable(file))
                        .map(validWskDeploy -> new WskDeployBinary(validWskDeploy.getPath(), validWskDeploy.getName()));
                if (wskDeployBinary.isPresent()) {
                    return wskDeployBinary.get();
                }
            }
        }
        return new NullWskDeployBinary();
    }

    private File[] findFiles(String parentDir, String fileName) {
        File dir = new File(parentDir);
        return dir.listFiles((dir1, name) -> name.startsWith(fileName));
    }

    private void expandAllNode(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private List<WskDeployManifest> loadWskDeployManifest(Project project) {
        List<WskDeployManifest> manifests = new ArrayList<>();
        Collection<VirtualFile> yamlFiles = FilenameIndex.getAllFilesByExt(project, "yaml", GlobalSearchScope.projectScope(project));
        Collection<VirtualFile> ymlFiles = FilenameIndex.getAllFilesByExt(project, "yml", GlobalSearchScope.projectScope(project));
        List<VirtualFile> files = Stream.of(yamlFiles, ymlFiles).flatMap(Collection::stream).collect(Collectors.toList());
        for (VirtualFile f : files) {
            String fullPath = f.getPath();
            String path = fullPath.replaceAll(project.getBasePath() + "/", "");
            String name = f.getName();
            manifests.add(new WskDeployManifest(path, fullPath, name));
        }
        return manifests;
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}
