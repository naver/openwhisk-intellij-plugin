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
import org.apache.openwhisk.intellij.explorer.toolwindow.action.DeleteTriggerAction;
import org.apache.openwhisk.intellij.explorer.toolwindow.action.EditTriggerAction;
import org.apache.openwhisk.intellij.explorer.toolwindow.action.OpenActivationViewAction;
import org.apache.openwhisk.intellij.explorer.toolwindow.action.OpenAndFireTriggerAction;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskAuth;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import org.apache.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;

import java.util.List;
import java.util.Optional;

public class WhiskTriggerGroup extends DefaultActionGroup {

    public WhiskTriggerGroup(WhiskAuth whiskAuth, WhiskTriggerMetaData whiskTriggerMetaData, List<WhiskEndpoint> endpoints) {
        add(new OpenAndFireTriggerAction(whiskAuth, whiskTriggerMetaData));
        add(new OpenActivationViewAction(endpoints, Optional.of(whiskAuth), Optional.empty(), Optional.of(whiskTriggerMetaData)));
        add(new EditTriggerAction(whiskAuth, whiskTriggerMetaData));
        add(new DeleteTriggerAction(whiskAuth, whiskTriggerMetaData));
    }
}
