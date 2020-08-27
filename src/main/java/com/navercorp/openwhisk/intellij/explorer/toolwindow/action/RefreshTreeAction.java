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

package com.navercorp.openwhisk.intellij.explorer.toolwindow.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;

import static com.intellij.icons.AllIcons.Actions.Refresh;

public class RefreshTreeAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(RefreshTreeAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    public RefreshTreeAction() {
        super(Refresh);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        EventUtils.publish(event.getProject(), RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
    }
}
