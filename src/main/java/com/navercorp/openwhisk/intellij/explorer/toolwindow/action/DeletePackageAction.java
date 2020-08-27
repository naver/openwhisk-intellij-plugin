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
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActionService;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskPackageService;
import com.navercorp.openwhisk.intellij.explorer.dialog.pkg.DeletePackageDialog;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.intellij.icons.AllIcons.Actions.GC;

public class DeletePackageAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(DeletePackageAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskAuth whiskAuth;
    private WhiskPackage whiskPackage;

    private WhiskPackageService whiskPackageService = WhiskPackageService.getInstance();
    private WhiskActionService whiskActionService = WhiskActionService.getInstance();

    public DeletePackageAction(WhiskAuth whiskAuth, WhiskPackage whiskPackage) {
        super("Delete", "Delete package", GC);
        this.whiskPackage = whiskPackage;
        this.whiskAuth = whiskAuth;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskAuth != null && whiskPackage != null) {
            try {
                whiskPackageService.getWhiskPackage(whiskAuth, whiskPackage.getNamespace(), whiskPackage.getName()).ifPresent(whiskPackageWithActions -> {
                    if ((new DeletePackageDialog(e.getProject(), whiskPackageWithActions)).showAndGet()) {
                        deletePackage(e, whiskPackageWithActions);
                    }
                });
            } catch (IOException ex) {
                final String msg = "The package cannot be deleted: " + whiskPackage.getName();
                LOG.error(msg, ex);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
            }
        }
    }

    private void deletePackage(AnActionEvent e, WhiskPackageWithActions whiskPackageWithActions) {
        try {
            /**
             * Delete actions
             */
            List<ExecutableWhiskAction> deletedActions = new ArrayList<>();
            if (!whiskPackageWithActions.getBinding().isPresent()) {
                deletedActions.addAll(deleteActions(e, whiskPackageWithActions));
            }

            /**
             * Delete package
             */
            Optional<WhiskPackageWithActions> deletedPackage = whiskPackageService.deleteWhiskPackage(whiskAuth, whiskPackage.getName());
            if (deletedPackage.isPresent()) {
                StringBuilder builder = new StringBuilder();
                builder.append("<html>");
                builder.append("The following entities were deleted<br/>");
                builder.append("- " + deletedPackage.get().getName() + "<br/>");
                for (ExecutableWhiskAction deleted : deletedActions) {
                    builder.append("- " + deletedPackage.get().getName() + "/" + deleted.getName() + "<br/>");
                }
                builder.append("</html>");
                String msg = builder.toString();
                LOG.info(msg);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.INFORMATION);

                ActionManager.getInstance().getAction("WhiskExplorer.Actions.Controls.Refresh").actionPerformed(e);
            } else {
                String msg = whiskPackageWithActions.getName() + " package can't be deleted";
                LOG.info(msg);
                NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
            }
        } catch (IOException ex) {
            String msg = whiskPackageWithActions.getName() + " package can't be deleted";
            LOG.error(msg, ex);
            NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
        }
    }

    private List<ExecutableWhiskAction> deleteActions(AnActionEvent e, WhiskPackageWithActions whiskPackageWithActions) {
        List<ExecutableWhiskAction> deletedActions = new ArrayList<>();
        try {
            for (CompactWhiskAction action : whiskPackageWithActions.getActions()) {
                whiskActionService.deleteWhiskActions(whiskAuth, Optional.of(whiskPackageWithActions.getName()), action.getName())
                        .ifPresent(deletedActions::add);
            }
        } catch (IOException ex) {
            String msg = whiskPackageWithActions.getName() + " action can't be deleted";
            LOG.error(msg, ex);
            NOTIFIER.notify(e.getProject(), msg, NotificationType.ERROR);
        }
        return deletedActions;
    }
}
