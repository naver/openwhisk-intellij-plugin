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
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddEndpointDialogForm {
    private static final Logger LOG = Logger.getInstance(AddEndpointDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JTextField aliasJTextField;
    private JTextField apihostJTextField;

    private WhiskService whiskService;
    private List<WhiskEndpoint> endpoints;

    private Project project;

    public AddEndpointDialogForm(Project project) {
        this.project = project;

        /**
         * Load endpoints
         */
        this.whiskService = ServiceManager.getService(project, WhiskService.class);
        try {
            endpoints = new ArrayList<>(JsonParserUtils.parseWhiskEndpoints(whiskService.getEndpoints())); // make mutable
        } catch (IOException e) {
            LOG.error("Endpoint parsing failed", e);
        }

    }

    public void addEndpoint() {
        final String alias = aliasJTextField.getText().trim();
        final String apihost = apihostJTextField.getText().trim(); // TODO validate api host

        if (existAlias(endpoints, alias)) {
            NOTIFIER.notify(project, "Failed to added endpoint: " + alias + " already exists.", NotificationType.ERROR);
        } else {
            endpoints.add(new WhiskEndpoint(alias, apihost, new ArrayList<>()));
            saveEndpoints(endpoints);

            // Update action tree
            EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
            NOTIFIER.notify(project, alias + " has been added successfully.", NotificationType.INFORMATION);
        }
    }

    private boolean existAlias(List<WhiskEndpoint> whiskEndpoints, String alias) {
        for (WhiskEndpoint ep : whiskEndpoints) {
            if (ep.getAlias().equals(alias)) {
                return true;
            }
        }
        return false;
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
