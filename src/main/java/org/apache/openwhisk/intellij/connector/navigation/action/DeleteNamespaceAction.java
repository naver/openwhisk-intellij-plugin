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

package org.apache.openwhisk.intellij.connector.navigation.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.apache.openwhisk.intellij.common.notification.SimpleNotifier;
import org.apache.openwhisk.intellij.common.service.WhiskService;
import org.apache.openwhisk.intellij.common.utils.EventUtils;
import org.apache.openwhisk.intellij.common.utils.JsonParserUtils;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import org.apache.openwhisk.intellij.connector.dialog.namespace.DeleteNamespaceDialog;
import org.apache.openwhisk.intellij.connector.navigation.listener.RefreshWhiskTreeListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.icons.AllIcons.General.Remove;

public class DeleteNamespaceAction extends AnAction {
    private final static Logger LOG = Logger.getInstance(DeleteNamespaceAction.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskNamespace whiskNamespace;

    public DeleteNamespaceAction(WhiskNamespace whiskNamespace) {
        super("Delete Namespace", "Delete Namespace", Remove);
        this.whiskNamespace = whiskNamespace;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskNamespace != null) {
            final Project project = e.getProject();
            DeleteNamespaceDialog dialog = new DeleteNamespaceDialog(project, whiskNamespace);
            if (dialog.showAndGet()) {

                /**
                 * Load endpoints
                 */
                WhiskService whiskService = ServiceManager.getService(project, WhiskService.class);
                List<WhiskEndpoint> endpoints = new ArrayList<>();
                try {
                    endpoints = new ArrayList<>(JsonParserUtils.parseWhiskEndpoints(whiskService.endpoints)); // make mutable
                } catch (IOException ex) {
                    LOG.error("Endpoint parsing failed", ex);
                }

                List<WhiskEndpoint> updatedEndpoints = removeNamespace(endpoints, whiskNamespace);
                saveEndpoints(whiskService, updatedEndpoints);

                // Update action tree
                EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
                NOTIFIER.notify(project, whiskNamespace.getPath() + " deleted successfully.", NotificationType.INFORMATION);
            }
        }
    }


    private List<WhiskEndpoint> removeNamespace(List<WhiskEndpoint> endpoints, WhiskNamespace whiskNamespace) {
        return endpoints.stream()
                .peek(ep -> {
                    List<WhiskNamespace> namespaces = ep.getNamespaces()
                            .stream()
                            .filter(ns -> !ns.getPath().equals(whiskNamespace.getPath()) && !ns.getAuth().equals(whiskNamespace.getAuth()))  // remove namespace
                            .collect(Collectors.toList());
                    ep.setNamespaces(namespaces);
                })
                .collect(Collectors.toList());
    }

    private void saveEndpoints(WhiskService whiskService, List<WhiskEndpoint> newEndpoints) {
        try {
            String eps = JsonParserUtils.writeEndpointsToJson(newEndpoints);
            whiskService.endpoints = eps;
            whiskService.loadState(whiskService);
        } catch (JsonProcessingException e) {
            LOG.error("Endpoint parsing failed", e);
        }
    }
}
