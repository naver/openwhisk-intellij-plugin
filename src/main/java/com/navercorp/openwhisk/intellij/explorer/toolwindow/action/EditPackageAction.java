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

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskPackageService;
import com.navercorp.openwhisk.intellij.explorer.dialog.pkg.PackageManagerDialog;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.intellij.icons.AllIcons.Actions.Edit;

public class EditPackageAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(EditPackageAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskAuth whiskAuth;
    private WhiskPackage whiskPackage;

    private WhiskPackageService whiskPackageService = WhiskPackageService.getInstance();

    public EditPackageAction(WhiskAuth whiskAuth, WhiskPackage whiskPackage) {
        super("Edit", "Edit Package", Edit);
        this.whiskAuth = whiskAuth;
        this.whiskPackage = whiskPackage;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null) {
            try {
                whiskPackageService.getWhiskPackage(whiskAuth, whiskPackage.getNamespace(), whiskPackage.getName())
                        .ifPresent(whiskPackageWithActions -> {
                            PackageManagerDialog dialog = new PackageManagerDialog(e.getProject(), whiskAuth, whiskPackageWithActions);
                            if (dialog.showAndGet()) {
                                LOG.info("PackageManagerDialog is closed");
                            }
                        });
            } catch (IOException ex) {
                final String msg = "The package cannot be loaded: " + whiskPackage.getName();
                LOG.error(msg, ex);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
            }
        }
    }
}
