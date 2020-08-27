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

package com.navercorp.openwhisk.intellij.explorer.dialog.endpoint.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.service.WhiskService;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditEndpointDialogForm {
    private static final Logger LOG = Logger.getInstance(EditEndpointDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JTextField aliasJTextField;
    private JTextField apihostJTextField;

    private WhiskService whiskService;
    private List<WhiskEndpoint> endpoints;

    private Project project;
    private WhiskEndpoint whiskEndpoint;

    public EditEndpointDialogForm(Project project, WhiskEndpoint whiskEndpoint) {
        this.project = project;
        this.whiskEndpoint = whiskEndpoint;

        /**
         * Load endpoints
         */
        this.whiskService = ServiceManager.getService(project, WhiskService.class);
        try {
            endpoints = new ArrayList<>(JsonParserUtils.parseWhiskEndpoints(whiskService.getEndpoints())); // make mutable
        } catch (IOException e) {
            LOG.error("Endpoint parsing failed", e);
        }

        aliasJTextField.setText(whiskEndpoint.getAlias());
        apihostJTextField.setText(whiskEndpoint.getApihost());
    }

    public void updateEndpoint() {
        final String alias = aliasJTextField.getText().trim();
        final String apihost = apihostJTextField.getText().trim(); // TODO validate api host

        if (existAlias(endpoints, whiskEndpoint.getAlias(), alias)) {
            NOTIFIER.notify(project, "Failed to update endpoint: " + alias + " already exists.", NotificationType.ERROR);
        } else {
            List<WhiskEndpoint> newEndpoints = updateWhiskEndpoint(endpoints,
                    whiskEndpoint.getAlias(),
                    new WhiskEndpoint(alias, apihost, whiskEndpoint.getNamespaces()));
            saveEndpoints(newEndpoints);

            // Update action tree
            EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
            NOTIFIER.notify(project, alias + " has been added successfully.", NotificationType.INFORMATION);
        }
    }

    private List<WhiskEndpoint> updateWhiskEndpoint(List<WhiskEndpoint> eps, String key, WhiskEndpoint newEndpoint) {
        for (int i = 0; i < eps.size(); i++) {
            if (eps.get(i).getAlias().equals(key)) {
                eps.set(i, newEndpoint);
            }
        }
        return eps;
    }

    private boolean existAlias(List<WhiskEndpoint> whiskEndpoints, String originAlias, String alias) {
        return whiskEndpoints.stream()
                .filter(ep -> !ep.getAlias().equals(originAlias)) // exclude current endpoint
                .anyMatch(ep -> ep.getAlias().equals(alias));     // find duplicated alias
    }

    private void saveEndpoints(List<WhiskEndpoint> newEndpoints) {
        try {
            String eps = JsonParserUtils.writeEndpointsToJson(newEndpoints);
            whiskService.setEndpoints(eps);
            whiskService.loadState(whiskService);
        } catch (JsonProcessingException e) {
            LOG.error("Endpoint parsing failed", e);
        }
    }


    public JPanel getContent() {
        return mainJPanel;
    }
}
