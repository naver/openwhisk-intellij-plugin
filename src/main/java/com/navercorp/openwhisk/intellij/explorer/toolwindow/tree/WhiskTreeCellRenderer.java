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

import com.intellij.ui.ColoredTreeCellRenderer;
import com.navercorp.openwhisk.intellij.common.Icons;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerRoot;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.intellij.icons.AllIcons.Modules.SourceRoot;
import static com.intellij.icons.AllIcons.Nodes.Module;
import static com.intellij.icons.AllIcons.Webreferences.Server;

public class WhiskTreeCellRenderer extends ColoredTreeCellRenderer {
    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
        Object userObject = treeNode.getUserObject();
        if (userObject instanceof WhiskEndpoint) {
            WhiskEndpoint whiskEndpoint = (WhiskEndpoint) userObject;
            setIcon(Server);
            append(whiskEndpoint.getAlias() + " (" + whiskEndpoint.getApihost() + ")");
        } else if (userObject instanceof WhiskNamespace) {
            WhiskNamespace whiskNamespace = (WhiskNamespace) userObject;
            setIcon(SourceRoot);
            append(whiskNamespace.getPath());
        } else if (userObject instanceof WhiskPackage) {
            WhiskPackage whiskPackage = (WhiskPackage) userObject;
            setIcon(Module);
            String boundMark = whiskPackage.getBinding().map(b -> " (from " + b.getNamespace() + "/" + b.getName() + ")").orElse("");
            append(whiskPackage.getName() + boundMark);
        } else if (userObject instanceof WhiskAction) {
            WhiskAction whiskAction = (WhiskAction) userObject;
            setIcon(whiskAction.getKindIcon());
            append(whiskAction.getName() + whiskAction.getKindExtension());
        } else if (userObject instanceof CompactWhiskAction) {
            CompactWhiskAction compactWhiskAction = (CompactWhiskAction) userObject;
            setIcon(compactWhiskAction.getKindIcon());
            append(compactWhiskAction.getName() + compactWhiskAction.getKindExtension());
        } else if (userObject instanceof WhiskTriggerRoot) {
            append("Triggers");
            setIcon(Icons.ENTITY_TRIGGER_ROOT);
        } else if (userObject instanceof WhiskTriggerMetaData) {
            WhiskTriggerMetaData whiskTriggerMetaData = (WhiskTriggerMetaData) userObject;
            setIcon(Icons.ENTITY_TRIGGER);
            append(whiskTriggerMetaData.getName());
        } else {
            append(userObject.toString());
        }
    }
}
