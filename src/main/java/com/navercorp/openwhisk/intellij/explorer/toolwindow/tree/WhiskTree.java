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

package com.navercorp.openwhisk.intellij.explorer.toolwindow.tree;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerRoot;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskPackageService;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class WhiskTree implements TreeModel {
    private static final Logger LOG = Logger.getInstance(WhiskTree.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private DefaultTreeModel innerModel;
    private DefaultMutableTreeNode root;
    private WhiskPackageService whiskPackageService;

    public WhiskTree(List<WhiskEndpoint> endpoints, WhiskPackageService whiskPackageService) {
        this.whiskPackageService = whiskPackageService;
        this.setTree(endpoints);
    }

    public void setTree(List<WhiskEndpoint> endpoints) {
        this.root = new DefaultMutableTreeNode("root");

        // endpoints
        for (WhiskEndpoint e : endpoints) {
            DefaultMutableTreeNode endPointNode = new DefaultMutableTreeNode(e);

            // namespaces
            for (WhiskNamespace ns : e.getNamespaces()) {
                DefaultMutableTreeNode namespaceNode = new DefaultMutableTreeNode(ns);
                endPointNode.add(namespaceNode);

                //triggers
                DefaultMutableTreeNode triggerRootNode = new DefaultMutableTreeNode(new WhiskTriggerRoot());
                namespaceNode.add(triggerRootNode);
                List<WhiskTriggerMetaData> triggers = ns.getTriggers();
                for (WhiskTriggerMetaData t : triggers) {
                    DefaultMutableTreeNode triggerNode = new DefaultMutableTreeNode(t);
                    triggerRootNode.add(triggerNode);
                }

                // packages
                List<WhiskPackage> packages = ns.getPackages();
                for (WhiskPackage p : packages) {
                    DefaultMutableTreeNode pkgNode = new DefaultMutableTreeNode(p);
                    namespaceNode.add(pkgNode);

                    // Add origin action of binding package to tree
                    p.getBinding().ifPresent(binding -> {
                        try {
                            whiskPackageService.getWhiskPackage(new WhiskAuth(ns.getAuth(), e.getApihost()), binding.getNamespace(), binding.getName())
                                    .ifPresent(packageWithActions ->
                                            packageWithActions.getActions()
                                                    .forEach(action -> {
                                                        DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(action);
                                                        pkgNode.add(actionNode);
                                                    }));
                        } catch (IOException ex) {
                            final String msg = binding.getNamespace() + "/" + binding.getName() + " package information cannot be retrieved.";
                            LOG.error(msg, ex);
                            NOTIFIER.notify(msg, NotificationType.ERROR);
                            ex.printStackTrace();
                        }
                    });
                }

                // actions
                List<WhiskActionMetaData> actions = ns.getActions();
                for (WhiskActionMetaData a : actions) {
                    DefaultMutableTreeNode actionNode = new DefaultMutableTreeNode(a);
                    if (a.getWhiskPackage().isPresent()) {
                        String pkgPath = a.getWhiskPackage().get();
                        Optional<DefaultMutableTreeNode> pkgNode = findPackageNode(namespaceNode, pkgPath);
                        if (pkgNode.isPresent()) {
                            pkgNode.get().add(actionNode);
                        } else {
                            namespaceNode.add(actionNode);
                        }
                    } else {
                        namespaceNode.add(actionNode);
                    }
                }
            }
            this.root.add(endPointNode);
        }

        this.innerModel = new DefaultTreeModel(root);
    }

    // Search 1 depth
    private Optional<DefaultMutableTreeNode> findPackageNode(DefaultMutableTreeNode ns, String pkgPath) {
        for (int i = 0; i < ns.getChildCount(); i++) {
            DefaultMutableTreeNode children = (DefaultMutableTreeNode) ns.getChildAt(i);
            if (children.getUserObject() instanceof WhiskPackage) {
                WhiskPackage existPkg = (WhiskPackage) children.getUserObject();
                if (pkgPath.equals(existPkg.getName())) {
                    return Optional.of(children);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Object getRoot() {
        return this.innerModel.getRoot();
    }

    @Override
    public Object getChild(Object parent, int index) {
        return this.innerModel.getChild(parent, index);
    }

    @Override
    public int getChildCount(Object parent) {
        return this.innerModel.getChildCount(parent);
    }

    @Override
    public boolean isLeaf(Object node) {
        return this.innerModel.isLeaf(node);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        this.innerModel.valueForPathChanged(path, newValue);

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return this.innerModel.getIndexOfChild(parent, child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        this.innerModel.addTreeModelListener(l);

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        this.innerModel.removeTreeModelListener(l);

    }
}
