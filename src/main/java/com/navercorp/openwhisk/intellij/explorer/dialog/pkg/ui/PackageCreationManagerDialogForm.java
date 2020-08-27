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

package com.navercorp.openwhisk.intellij.explorer.dialog.pkg.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskPackageService;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;

import javax.swing.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class PackageCreationManagerDialogForm {
    private static final Logger LOG = Logger.getInstance(PackageCreationManagerDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JTextField nameJTextField;
    private JPanel mainJPanel;

    private WhiskPackageService whiskPackageService = WhiskPackageService.getInstance();

    private Project project;
    private WhiskAuth whiskAuth;

    public PackageCreationManagerDialogForm(Project project, WhiskAuth whiskAuth) {
        this.project = project;
        this.whiskAuth = whiskAuth;
    }

    public void createPackage() {
        try {
            String name = nameJTextField.getText().trim();

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("name", name);
            payload.put("namespace", "_");

            Optional<WhiskPackageWithActions> created = whiskPackageService.createWhiskPackage(whiskAuth, name, payload);
            if (created.isPresent()) {
                NOTIFIER.notify(project, name + " created", NotificationType.INFORMATION);
            } else {
                NOTIFIER.notify(project, name + " already exist", NotificationType.ERROR);
            }
        } catch (IOException e) {
            String msg = "Failed to create package: ";
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}
