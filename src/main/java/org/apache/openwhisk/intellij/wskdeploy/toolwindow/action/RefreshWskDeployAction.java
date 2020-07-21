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

package org.apache.openwhisk.intellij.wskdeploy.toolwindow.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.openwhisk.intellij.common.notification.SimpleNotifier;
import org.apache.openwhisk.intellij.common.utils.EventUtils;
import org.apache.openwhisk.intellij.wskdeploy.toolwindow.listener.RefreshWskDeployManifestListener;

import static com.intellij.icons.AllIcons.Actions.Refresh;

public class RefreshWskDeployAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(RefreshWskDeployAction.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    public RefreshWskDeployAction() {
        super(Refresh);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        EventUtils.publish(event.getProject(), RefreshWskDeployManifestListener.TOPIC, RefreshWskDeployManifestListener::refreshWskDeployManifest);
    }
}
