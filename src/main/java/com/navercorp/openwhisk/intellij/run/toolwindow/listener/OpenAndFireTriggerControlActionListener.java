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

package com.navercorp.openwhisk.intellij.run.toolwindow.listener;

import com.intellij.util.messages.Topic;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;

import java.util.EventListener;

public interface OpenAndFireTriggerControlActionListener extends EventListener {

    Topic<OpenAndFireTriggerControlActionListener> TOPIC = Topic.create("Open and Fire Trigger Control", OpenAndFireTriggerControlActionListener.class);

    void openAndFireTriggerControlWindow(WhiskAuth whiskAuth, ExecutableWhiskTrigger executableWhiskTrigger);

}
