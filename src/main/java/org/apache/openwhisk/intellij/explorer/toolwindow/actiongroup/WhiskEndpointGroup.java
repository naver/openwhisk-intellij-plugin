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

package org.apache.openwhisk.intellij.explorer.toolwindow.actiongroup;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.apache.openwhisk.intellij.explorer.toolwindow.action.AddNamespaceAction;
import org.apache.openwhisk.intellij.explorer.toolwindow.action.DeleteEndpointAction;
import org.apache.openwhisk.intellij.explorer.toolwindow.action.EditEndpointAction;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskEndpoint;

public class WhiskEndpointGroup extends DefaultActionGroup {

    public WhiskEndpointGroup(WhiskEndpoint whiskEndpoint) {
        add(new EditEndpointAction(whiskEndpoint));
        add(new DeleteEndpointAction(whiskEndpoint));
        add(new AddNamespaceAction(whiskEndpoint));
    }
}
