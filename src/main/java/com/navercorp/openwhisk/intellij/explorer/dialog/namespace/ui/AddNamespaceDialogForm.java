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

package com.navercorp.openwhisk.intellij.explorer.dialog.namespace.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.navercorp.openwhisk.intellij.common.service.WhiskService;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskNamespaceService;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddNamespaceDialogForm {
    private static final Logger LOG = Logger.getInstance(AddNamespaceDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JPasswordField authKeyJPasswordField;
    private JButton verifyAuthKeyJButton;
    private JLabel verifyResultJLabel;
    private JCheckBox showAuthKeyJCheckBox;

    private WhiskNamespaceService whiskNamespaceService = WhiskNamespaceService.getInstance();
    private WhiskService whiskService;
    private List<WhiskEndpoint> endpoints;

    private Project project;
    private WhiskEndpoint whiskEndpoint;

    public AddNamespaceDialogForm(Project project, WhiskEndpoint whiskEndpoint) {
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

        verifyAuthKeyJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final String auth = new String(authKeyJPasswordField.getPassword());
                final String apihost = whiskEndpoint.getApihost();
                final WhiskAuth newWhiskAuth = new WhiskAuth(auth, apihost);
                Optional<WhiskNamespace> result = whiskNamespaceService.validateNamespace(newWhiskAuth);
                if (result.isPresent()) {
                    verifyResultJLabel.setText("Success");
                    verifyResultJLabel.setForeground(JBColor.GREEN);
                } else {
                    verifyResultJLabel.setText("Invalid authkey");
                    verifyResultJLabel.setForeground(JBColor.RED);
                }
            }
        });

        showAuthKeyJCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                authKeyJPasswordField.setEchoChar((char) 0);
            } else {
                authKeyJPasswordField.setEchoChar('*');
            }
        });
    }

    public void addNamespace() {
        final String auth = new String(authKeyJPasswordField.getPassword());
        final String apihost = whiskEndpoint.getApihost();
        final WhiskAuth newWhiskAuth = new WhiskAuth(auth, apihost);
        Optional<WhiskNamespace> result = whiskNamespaceService.validateNamespace(newWhiskAuth);
        if (result.isPresent()) {
            WhiskNamespace whiskNamespace = result.get();
            List<WhiskEndpoint> newEndpoints = addNamespace(whiskEndpoint, whiskNamespace);
            saveEndpoints(newEndpoints);

            // Update action tree
            EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
            NOTIFIER.notify(project, whiskNamespace.getPath() + " has been added successfully.", NotificationType.INFORMATION);
        } else {
            NOTIFIER.notify(project, "Invalid authkey", NotificationType.ERROR);
        }
    }

    private List<WhiskEndpoint> addNamespace(WhiskEndpoint endpoint, WhiskNamespace whiskNamespace) {
        for (int i = 0; i < endpoints.size(); i++) {
            WhiskEndpoint ep = endpoints.get(i);
            if (ep.getAlias().equals(endpoint.getAlias()) && ep.getApihost().equals(endpoint.getApihost())) {
                List<WhiskNamespace> namespaces = ep.getNamespaces();
                namespaces.add(whiskNamespace);
                ep.setNamespaces(namespaces);
            }
            endpoints.set(i, ep);
        }
        return endpoints;
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
