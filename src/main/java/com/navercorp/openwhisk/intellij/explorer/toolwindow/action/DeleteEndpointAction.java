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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.service.WhiskService;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.explorer.dialog.endpoint.DeleteEndpointDialog;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.icons.AllIcons.General.Remove;

public class DeleteEndpointAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(DeleteEndpointAction.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private WhiskEndpoint whiskEndpoint;

    public DeleteEndpointAction(WhiskEndpoint whiskEndpoint) {
        super("Delete Endpoint", "Delete Endpoint", Remove);
        this.whiskEndpoint = whiskEndpoint;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (whiskEndpoint != null) {
            final Project project = e.getProject();
            DeleteEndpointDialog dialog = new DeleteEndpointDialog(project, whiskEndpoint);
            if (dialog.showAndGet()) {

                /**
                 * Load endpoints
                 */
                WhiskService whiskService = ServiceManager.getService(project, WhiskService.class);
                List<WhiskEndpoint> endpoints = new ArrayList<>();
                try {
                    endpoints = new ArrayList<>(JsonParserUtils.parseWhiskEndpoints(whiskService.getEndpoints())); // make mutable
                } catch (IOException ex) {
                    LOG.error("Endpoint parsing failed", ex);
                }

                List<WhiskEndpoint> updatedEndpoints = removeEndpoint(endpoints, whiskEndpoint);
                saveEndpoints(whiskService, updatedEndpoints);

                // Update action tree
                EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
                NOTIFIER.notify(project, whiskEndpoint.getAlias() + " deleted successfully.", NotificationType.INFORMATION);
            }
        }
    }


    private List<WhiskEndpoint> removeEndpoint(List<WhiskEndpoint> endpoints, WhiskEndpoint endpoint) {
        return endpoints.stream()
                .filter(ep -> !ep.getAlias().equals(endpoint.getAlias())) // remove endpoint
                .collect(Collectors.toList());
    }

    private void saveEndpoints(WhiskService whiskService, List<WhiskEndpoint> newEndpoints) {
        try {
            String eps = JsonParserUtils.writeEndpointsToJson(newEndpoints);
            whiskService.setEndpoints(eps);
            whiskService.loadState(whiskService);
        } catch (JsonProcessingException e) {
            LOG.error("Endpoint parsing failed", e);
        }
    }
}
