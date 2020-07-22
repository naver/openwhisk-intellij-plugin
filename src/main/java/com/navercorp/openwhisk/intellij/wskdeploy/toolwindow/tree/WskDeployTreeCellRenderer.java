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

import com.intellij.ui.ColoredTreeCellRenderer;
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.intellij.icons.AllIcons.Actions.Execute;
import static com.intellij.icons.AllIcons.Actions.Rollback;
import static com.navercorp.openwhisk.intellij.common.Icons.RUN_ANYTHING;
import static com.navercorp.openwhisk.intellij.common.Icons.YAML;

public class WskDeployTreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
        Object userObject = treeNode.getUserObject();
        if (userObject instanceof WskDeployManifest) {
            WskDeployManifest manifest = (WskDeployManifest) userObject;
            setIcon(YAML);
            append(manifest.getPath());
        } else if (userObject instanceof WskDeployCmdDeploy) {
            WskDeployCmdDeploy wskDeployCmdDeploy = (WskDeployCmdDeploy) userObject;
            setIcon(Execute);
            append(wskDeployCmdDeploy.getCmdName());
        } else if (userObject instanceof WskDeployCmdUndeploy) {
            WskDeployCmdUndeploy wskDeployCmdUndeploy = (WskDeployCmdUndeploy) userObject;
            setIcon(Rollback);
            append(wskDeployCmdUndeploy.getCmdName());
        } else if (userObject instanceof WskDeployBinary) {
            WskDeployBinary wskDeployBinary = (WskDeployBinary) userObject;
            setIcon(RUN_ANYTHING);
            append("wskdeploy (" + wskDeployBinary.getFullPath() + ")");
        } else if (userObject instanceof NullWskDeployBinary) {
            setIcon(RUN_ANYTHING);
            append("There is no registered wskdeploy binary. Please find the file and register.");
        } else {
            append(userObject.toString());
        }
    }
}
