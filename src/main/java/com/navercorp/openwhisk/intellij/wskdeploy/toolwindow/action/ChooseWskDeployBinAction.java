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

package com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.action;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.ValidationUtils;
import com.navercorp.openwhisk.intellij.wskdeploy.toolwindow.listener.ChooseWskDeployBinaryListener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.navercorp.openwhisk.intellij.common.Icons.OPEN_DISK_HOVER;

public class ChooseWskDeployBinAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(ChooseWskDeployBinAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    ChooseWskDeployBinAction() {
        super(OPEN_DISK_HOVER);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile file = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileDescriptor(), e.getProject(), null);
        if (file == null) {
            return;
        }

        Optional<VirtualFile> validWskDeploy = ValidationUtils.validateWskDeploy(Optional.of(file));
        if (validWskDeploy.isPresent()) {
            NOTIFIER.notify(e.getProject(), file.getName() + " file has been registered with wskdeploy.", NotificationType.INFORMATION);
            EventUtils.publish(e.getProject(), ChooseWskDeployBinaryListener.TOPIC, l -> l.chooseWskDeployBinary(validWskDeploy.get()));
        } else {
            NOTIFIER.notify(e.getProject(), file.getName() + " file cannot be used with wskdeploy.", NotificationType.ERROR);
        }
    }
}
