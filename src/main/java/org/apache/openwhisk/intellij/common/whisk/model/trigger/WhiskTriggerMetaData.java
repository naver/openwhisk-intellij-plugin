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

package org.apache.openwhisk.intellij.common.whisk.model.trigger;

import org.apache.openwhisk.intellij.explorer.editor.model.ComboBoxEntityEntry;
import org.apache.openwhisk.intellij.explorer.editor.model.ComboBoxEntityType;

import java.util.List;
import java.util.Map;

public class WhiskTriggerMetaData extends WhiskTrigger {

    public WhiskTriggerMetaData() {
    }

    public WhiskTriggerMetaData(String name, String namespace, String version, long updated, boolean publish, List<Map<String, Object>> annotations) {
        super(name, namespace, version, updated, publish, annotations);
    }

    public ComboBoxEntityEntry toCombBoxEntityEntry() {
        return new ComboBoxEntityEntry(getName(), ComboBoxEntityType.TRIGGER);
    }
}
