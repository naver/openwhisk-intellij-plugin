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

package com.navercorp.openwhisk.intellij.explorer.toolwindow.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.service.WhiskService;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.FileUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerRoot;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskPackageService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.actiongroup.*;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.OpenActionControlActionListener;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.OpenTriggerControlActionListener;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.tree.WhiskTree;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.tree.WhiskTreeCellRenderer;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public class WhiskExplorerWindowForm {
    private static final Logger LOG = Logger.getInstance(WhiskExplorerWindowForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JPanel actionsJPanel;
    private JScrollPane contentsJPanel;
    private JTree whiskJTree;

    private FileEditorManager fileEditorManager;
    private Project project;

    private List<WhiskEndpoint> endpoints = new ArrayList<>();

    public WhiskExplorerWindowForm(Project project, ToolWindow toolWindow) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);

        String actionGroupName = "WhiskExplorer.Actions.Controls";
        ActionManager actionManager = ActionManager.getInstance();
        ActionGroup actionGroup = (ActionGroup) actionManager.getAction(actionGroupName);
        ActionToolbar actionToolbar = actionManager.createActionToolbar("", actionGroup, true);

        actionToolbar.setTargetComponent(actionsJPanel);
        actionsJPanel.add(actionToolbar.getComponent());

        WhiskService service = ServiceManager.getService(project, WhiskService.class);
        WhiskActionService whiskActionService = WhiskActionService.getInstance();
        WhiskPackageService whiskPackageService = WhiskPackageService.getInstance();
        WhiskTriggerService whiskTriggerService = WhiskTriggerService.getInstance();

        setWhiskTree(service, whiskActionService, whiskPackageService, whiskTriggerService);
    }

    private void setWhiskTree(WhiskService service,
                              WhiskActionService whiskActionService,
                              WhiskPackageService whiskPackageService,
                              WhiskTriggerService whiskTriggerService) {

        if (StringUtils.isNotEmpty(service.getEndpoints())) {
            try {
                endpoints = getEntities(whiskPackageService,
                        whiskActionService,
                        whiskTriggerService,
                        JsonParserUtils.parseWhiskEndpoints(service.getEndpoints()));
            } catch (IOException e) {
                final String msg = "Failed to parsing endpoints: " + service.getEndpoints();
                LOG.error(msg, e);
                NOTIFIER.notify(project, msg, NotificationType.ERROR);
            }
        }

        whiskJTree.setModel(new WhiskTree(endpoints, whiskPackageService));
        whiskJTree.setRootVisible(false);
        whiskJTree.setCellRenderer(new WhiskTreeCellRenderer());
        expandToNamespace(whiskJTree);
        whiskJTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1) {
                    if (whiskJTree == null) {
                        return;
                    }

                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) whiskJTree.getLastSelectedPathComponent();
                    if (node == null) {
                        return;
                    }

                    Object userObject = node.getUserObject();
                    if (userObject instanceof WhiskEndpoint) {
                        WhiskEndpoint whiskEndpoint = (WhiskEndpoint) userObject;
                        LOG.info(whiskEndpoint.getAlias());
                    } else if (userObject instanceof WhiskNamespace) {
                        WhiskNamespace whiskNamespace = (WhiskNamespace) userObject;
                        LOG.info(whiskNamespace.getPath());
                    } else if (userObject instanceof WhiskPackage) {
                        WhiskPackage whiskPackage = (WhiskPackage) userObject;
                        LOG.info(whiskPackage.getName());
                    } else if (userObject instanceof WhiskActionMetaData) {
                        WhiskActionMetaData whiskAction = (WhiskActionMetaData) userObject;
                        LOG.info(whiskAction.getName());

                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                        if (parentNode.getUserObject() instanceof WhiskPackage) {
                            // TODO refactor getAuthFromTreeNode method
                            WhiskPackage pkg = (WhiskPackage) parentNode.getUserObject();

                            DefaultMutableTreeNode namespaceNode = (DefaultMutableTreeNode) parentNode.getParent();
                            WhiskNamespace namespace = (WhiskNamespace) namespaceNode.getUserObject();

                            DefaultMutableTreeNode endpointNode = (DefaultMutableTreeNode) namespaceNode.getParent();
                            WhiskEndpoint endpoint = (WhiskEndpoint) endpointNode.getUserObject();

                            WhiskAuth auth = new WhiskAuth(namespace.getAuth(), endpoint.getApihost());

                            try {
                                whiskActionService.getWhiskAction(auth,
                                        Optional.ofNullable(namespace.getPath()),
                                        Optional.ofNullable(pkg.getName()), whiskAction.getName())
                                        .ifPresent(executableWhiskAction -> openEditorAndWhiskRunWindow(auth, executableWhiskAction));
                            } catch (IOException e1) {
                                final String msg1 = "The action cannot be opened.";
                                LOG.error(msg1, e1);
                                NOTIFIER.notify(project, msg1, NotificationType.ERROR);
                            }
                        } else if (parentNode.getUserObject() instanceof WhiskNamespace) {
                            WhiskNamespace namespace = (WhiskNamespace) parentNode.getUserObject();

                            DefaultMutableTreeNode endpointNode = (DefaultMutableTreeNode) parentNode.getParent();
                            WhiskEndpoint endpoint = (WhiskEndpoint) endpointNode.getUserObject();

                            WhiskAuth auth = new WhiskAuth(namespace.getAuth(), endpoint.getApihost());

                            try {
                                whiskActionService.getWhiskAction(auth, Optional.ofNullable(namespace.getPath()), Optional.empty(), whiskAction.getName())
                                        .ifPresent(executableWhiskAction -> openEditorAndWhiskRunWindow(auth, executableWhiskAction));
                            } catch (IOException e1) {
                                final String msg1 = "The action cannot be opened.";
                                LOG.error(msg1, e1);
                                NOTIFIER.notify(project, msg1, NotificationType.ERROR);
                            }

                        } else {
                            LOG.error("[WhiskActionMetaData] The action cannot be loaded: " + whiskAction.getFullyQualifiedName());
                        }
                    } else if (userObject instanceof CompactWhiskAction) {
                        CompactWhiskAction action = (CompactWhiskAction) userObject;
                        LOG.info(action.getName());

                        DefaultMutableTreeNode boundPkgNode = (DefaultMutableTreeNode) node.getParent();
                        if (boundPkgNode.getUserObject() instanceof WhiskPackage) {
                            WhiskPackage boundPkg = (WhiskPackage) boundPkgNode.getUserObject();

                            DefaultMutableTreeNode namespaceNode = (DefaultMutableTreeNode) boundPkgNode.getParent();
                            WhiskNamespace namespace = (WhiskNamespace) namespaceNode.getUserObject();

                            DefaultMutableTreeNode endpointNode = (DefaultMutableTreeNode) namespaceNode.getParent();
                            WhiskEndpoint endpoint = (WhiskEndpoint) endpointNode.getUserObject();

                            WhiskAuth auth = new WhiskAuth(namespace.getAuth(), endpoint.getApihost());

                            boundPkg.getBinding().ifPresent(p -> {
                                try {
                                    //When clicked, the code is fetched from the remote server.
                                    whiskActionService.getWhiskAction(auth,
                                            Optional.ofNullable(p.getNamespace()),
                                            Optional.ofNullable(p.getName()), action.getName())
                                            .ifPresent(executableWhiskAction -> openEditorAndWhiskRunWindow(auth, executableWhiskAction));
                                } catch (IOException e1) {
                                    final String msg1 = "The action cannot be opened.";
                                    LOG.error(msg1, e1);
                                    NOTIFIER.notify(project, msg1, NotificationType.ERROR);
                                }

                            });
                        } else {
                            LOG.error("[CompactWhiskAction] The action cannot be loaded: " + action.getName());
                        }
                    } else if (userObject instanceof WhiskTriggerRoot) {
                        // Nothing to do
                        LOG.debug("[WhiskTriggerRoot] is selected");
                    } else if (userObject instanceof WhiskTriggerMetaData) {
                        DefaultMutableTreeNode namespaceNode = (DefaultMutableTreeNode) node.getParent().getParent();

                        WhiskNamespace namespace = (WhiskNamespace) namespaceNode.getUserObject();

                        DefaultMutableTreeNode endpointNode = (DefaultMutableTreeNode) namespaceNode.getParent();
                        WhiskEndpoint endpoint = (WhiskEndpoint) endpointNode.getUserObject();

                        WhiskAuth auth = new WhiskAuth(namespace.getAuth(), endpoint.getApihost());

                        WhiskTriggerMetaData whiskTriggerMetaData = (WhiskTriggerMetaData) userObject;
                        LOG.info(whiskTriggerMetaData.getName());
                        try {
                            whiskTriggerService.getWhiskTrigger(auth, whiskTriggerMetaData.getName()).ifPresent(executableWhiskTrigger ->
                                    EventUtils.publish(project,
                                            OpenTriggerControlActionListener.TOPIC,
                                            (l) -> l.openTriggerControlWindow(auth, executableWhiskTrigger)));
                        } catch (IOException ex) {
                            final String msg = "The trigger cannot be loaded: " + whiskTriggerMetaData.getName();
                            LOG.error(msg, ex);
                            NOTIFIER.notify(project, msg, NotificationType.ERROR);
                        }
                    } else {
                        LOG.error("The unknown object of tree: " + userObject.toString());
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (whiskJTree == null) {
                        return;
                    }

                    TreePath path = whiskJTree.getPathForLocation(e.getX(), e.getY());
                    if (path == null) {
                        return;
                    }

                    whiskJTree.setSelectionPath(path);
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.getUserObject() instanceof WhiskEndpoint) {
                        WhiskEndpoint whiskEndpoint = (WhiskEndpoint) node.getUserObject();
                        ActionGroup actionGroup = new WhiskEndpointGroup(whiskEndpoint);
                        ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", actionGroup);
                        actionPopupMenu.getComponent().show(whiskJTree, e.getX(), e.getY());
                    } else if (node.getUserObject() instanceof WhiskNamespace) {
                        getAuthFromTreeNode(node).ifPresent(auth -> {
                            WhiskNamespace whiskNamespace = (WhiskNamespace) node.getUserObject();
                            ActionGroup actionGroup = new WhiskNamespaceGroup(auth, whiskNamespace, endpoints);
                            ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", actionGroup);
                            actionPopupMenu.getComponent().show(whiskJTree, e.getX(), e.getY());
                        });
                    } else if (node.getUserObject() instanceof WhiskActionMetaData) {
                        WhiskActionMetaData whiskActionMetaData = (WhiskActionMetaData) node.getUserObject();
                        getAuthFromTreeNode(node).ifPresent(auth -> {
                            ActionGroup actionGroup = new WhiskActionGroup(auth, whiskActionMetaData, endpoints);
                            ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", actionGroup);
                            actionPopupMenu.getComponent().show(whiskJTree, e.getX(), e.getY());
                        });
                    } else if (node.getUserObject() instanceof CompactWhiskAction) {
                        CompactWhiskAction compactWhiskAction = (CompactWhiskAction) node.getUserObject();
                        WhiskPackage pkg = (WhiskPackage) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
                        WhiskNamespace ns = (WhiskNamespace) ((DefaultMutableTreeNode) node.getParent().getParent()).getUserObject();

                        getAuthFromTreeNode(node).ifPresent(auth -> {
                            try {
                                whiskActionService.getWhiskAction(auth, Optional.of(ns.getPath()), Optional.of(pkg.getName()), compactWhiskAction.getName())
                                        .ifPresent(action -> {
                                            ActionGroup actionGroup = new WhiskActionGroup(auth, toBindingWhiskActionMetaData(pkg, action), endpoints);
                                            ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", actionGroup);
                                            actionPopupMenu.getComponent().show(whiskJTree, e.getX(), e.getY());
                                        });
                            } catch (IOException ex) {
                                final String msg = "The action cannot be loaded.";
                                LOG.error(msg, ex);
                                NOTIFIER.notify(project, msg, NotificationType.ERROR);
                            }
                        });
                    } else if (node.getUserObject() instanceof WhiskTriggerMetaData) {
                        WhiskTriggerMetaData whiskTriggerMetaData = (WhiskTriggerMetaData) node.getUserObject();
                        getAuthFromTreeNode(node).ifPresent(auth -> {
                            ActionGroup actionGroup = new WhiskTriggerGroup(auth, whiskTriggerMetaData, endpoints);
                            ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", actionGroup);
                            actionPopupMenu.getComponent().show(whiskJTree, e.getX(), e.getY());
                        });
                    } else if (node.getUserObject() instanceof WhiskTriggerRoot) {
                        getAuthFromTreeNode(node).ifPresent(auth -> {
                            ActionGroup actionGroup = new WhiskTriggerRootGroup(auth);
                            ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", actionGroup);
                            actionPopupMenu.getComponent().show(whiskJTree, e.getX(), e.getY());
                        });
                    } else if (node.getUserObject() instanceof WhiskPackage) {
                        getAuthFromTreeNode(node).ifPresent(auth -> {
                            WhiskPackage whiskPackage = (WhiskPackage) node.getUserObject();
                            ActionGroup actionGroup = new WhiskPackageGroup(auth, whiskPackage);
                            ActionPopupMenu actionPopupMenu = ActionManager.getInstance().createActionPopupMenu("", actionGroup);
                            actionPopupMenu.getComponent().show(whiskJTree, e.getX(), e.getY());
                        });
                    }
                }
            }
        });

        EventUtils.subscribe(project, project, RefreshWhiskTreeListener.TOPIC, () -> {
            if (whiskJTree != null) {
                try {
                    List<WhiskEndpoint> whiskEndpoints = JsonParserUtils.parseWhiskEndpoints(service.getEndpoints());
                    endpoints = getEntities(whiskPackageService, whiskActionService, whiskTriggerService, whiskEndpoints);
                    whiskJTree.setModel(new WhiskTree(endpoints, whiskPackageService));
                    expandToNamespace(whiskJTree);

                    if (whiskEndpoints.isEmpty()) {
                        final String msg = "There are no endpoints saved.";
                        LOG.info(msg);
                        NOTIFIER.notify(project, msg, NotificationType.INFORMATION);
                    }
                } catch (IOException e) {
                    final String msg = "Failed to parsing endpoints: " + service.getEndpoints();
                    LOG.error(msg, e);
                    NOTIFIER.notify(project, msg, NotificationType.ERROR);
                }
            }
        });
    }

    private WhiskActionMetaData toBindingWhiskActionMetaData(WhiskPackage whiskPackage, ExecutableWhiskAction action) {
        return new WhiskActionMetaData(action.getName(),
                whiskPackage.getNamespace() + "/" + whiskPackage.getName(),
                action.getVersion(),
                action.getUpdated(),
                action.isPublish(),
                action.getAnnotations(),
                action.getLimits(),
                action.getExec().toExecMetaData());
    }


    private void expandToNamespace(JTree tree) {
        final Enumeration<?> topLevelNodes = ((TreeNode) tree.getModel().getRoot()).children();
        final int level = 2;  // The level is the distance from the root to this node.
        expandNode(tree, topLevelNodes, level);
    }

    private void expandNode(JTree tree, Enumeration<?> nodes, int level) {
        while (nodes.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();
            tree.expandPath(new TreePath(node.getPath()));
            if (node.getLevel() < level) {
                expandNode(tree, node.children(), level);
            }
        }
    }

    private void openEditorAndWhiskRunWindow(WhiskAuth auth, ExecutableWhiskAction executableWhiskAction) {
        try {
            final String tmpFilePath = project.getBasePath() + "/.idea/openwhisk";

            if (!executableWhiskAction.isSequenceAction()) {
                /**
                 * Open Editor
                 */
                VirtualFile actionFile = FileUtils.writeActionToFile(tmpFilePath, executableWhiskAction);
                fileEditorManager.openFile(actionFile, true);
            }

            /**
             * Open Whisk Run Window
             */
            EventUtils.publish(project, OpenActionControlActionListener.TOPIC, (l) -> l.openActionControlWindow(auth, executableWhiskAction));
        } catch (IOException e1) {
            final String msg1 = "The action cannot be opened.";
            LOG.error(msg1, e1);
            NOTIFIER.notify(project, msg1, NotificationType.ERROR);
        }

    }

    private Optional<WhiskAuth> getAuthFromTreeNode(DefaultMutableTreeNode node) {
        try {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            if (node.getUserObject() instanceof WhiskNamespace
                    && parent.getUserObject() instanceof WhiskEndpoint) {
                WhiskNamespace namespace = (WhiskNamespace) node.getUserObject();
                WhiskEndpoint endpoint = (WhiskEndpoint) parent.getUserObject();
                return Optional.of(new WhiskAuth(namespace.getAuth(), endpoint.getApihost()));
            } else if (node.getUserObject() instanceof WhiskActionMetaData
                    || node.getUserObject() instanceof CompactWhiskAction
                    || node.getUserObject() instanceof WhiskTriggerMetaData
                    || node.getUserObject() instanceof WhiskTriggerRoot
                    || node.getUserObject() instanceof WhiskPackage
            ) {
                return getAuthFromTreeNode(parent);
            }
        } catch (Exception e) {
            LOG.error(e);
            return Optional.empty();
        }
        return Optional.empty();
    }

    private List<WhiskEndpoint> getEntities(WhiskPackageService whiskPackageService, WhiskActionService
            whiskActionService, WhiskTriggerService whiskTriggerService, List<WhiskEndpoint> whiskEndpoints) {
        List<WhiskEndpoint> newWhiskEndpoints = new ArrayList<>();
        for (WhiskEndpoint ep : whiskEndpoints) {
            List<WhiskNamespace> newNamespaces = new ArrayList<>();
            for (WhiskNamespace ns : ep.getNamespaces()) {
                try {
                    WhiskAuth auth = new WhiskAuth(ns.getAuth(), ep.getApihost());
                    // get pkg, action
                    ns.setPackages(whiskPackageService.getWhiskPackages(auth));
                    ns.setActions(whiskActionService.getWhiskActions(auth));
                    ns.setTriggers(whiskTriggerService.getWhiskTriggers(auth));

                    newNamespaces.add(ns);
                } catch (IOException e) {
                    final String msg = ns.getPath() + " entities cannot be loaded.";
                    LOG.error(msg, e);
                    NOTIFIER.notify(project, msg, NotificationType.ERROR);
                }
            }
            ep.setNamespaces(newNamespaces);
            newWhiskEndpoints.add(ep);
        }
        return newWhiskEndpoints;
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}

