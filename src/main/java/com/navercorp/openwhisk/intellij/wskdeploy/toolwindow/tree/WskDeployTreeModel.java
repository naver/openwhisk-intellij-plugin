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

package com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.tree;

import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.*;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Optional;

public class WskDeployTreeModel implements TreeModel {
    private static final Logger LOG = Logger.getInstance(WskDeployTreeModel.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private DefaultTreeModel innerModel;
    private DefaultMutableTreeNode root;

    public WskDeployTreeModel(WskDeployFile wskDeployFile, List<WskDeployManifest> manifests) {
        setTree(wskDeployFile, manifests);
    }

    public void setTree(WskDeployFile wskDeployFile, List<WskDeployManifest> manifests) {
        this.root = new DefaultMutableTreeNode(wskDeployFile);

        for (WskDeployManifest manifest : manifests) {
            DefaultMutableTreeNode manifestNode = new DefaultMutableTreeNode(manifest);
            root.add(manifestNode);

            Optional<WskDeployBinary> wskDeployBinary = toWskDeployBinary(wskDeployFile);
            manifestNode.add(new DefaultMutableTreeNode(new WskDeployCmdDeploy(wskDeployBinary, manifest)));
            manifestNode.add(new DefaultMutableTreeNode(new WskDeployCmdUndeploy(wskDeployBinary, manifest)));
        }

        this.innerModel = new DefaultTreeModel(this.root);
    }

    private Optional<WskDeployBinary> toWskDeployBinary(WskDeployFile wskDeployFile) {
        if (wskDeployFile instanceof WskDeployBinary) {
            return Optional.of((WskDeployBinary) wskDeployFile);
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
