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

package org.apache.openwhisk.intellij.connector.navigation.actiongroup;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.apache.openwhisk.intellij.connector.navigation.action.DeleteActionAction;
import org.apache.openwhisk.intellij.connector.navigation.action.EditActionAction;
import org.apache.openwhisk.intellij.connector.navigation.action.OpenActivationViewAction;
import org.apache.openwhisk.intellij.connector.navigation.action.OpenAndRunActionAction;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskAuth;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import org.apache.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;

import java.util.List;
import java.util.Optional;

public class WhiskActionGroup extends DefaultActionGroup {

    public WhiskActionGroup(WhiskAuth whiskAuth, WhiskActionMetaData whiskActionMetaData, List<WhiskEndpoint> endpoints) {
        add(new OpenAndRunActionAction(whiskAuth, whiskActionMetaData));
        add(new OpenActivationViewAction(endpoints, Optional.of(whiskAuth), Optional.of(whiskActionMetaData), Optional.empty()));
        add(new EditActionAction(whiskAuth, whiskActionMetaData));
        add(new DeleteActionAction(whiskAuth, whiskActionMetaData));
    }
}
