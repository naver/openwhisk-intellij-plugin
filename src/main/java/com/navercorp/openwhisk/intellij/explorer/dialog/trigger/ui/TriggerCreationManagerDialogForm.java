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

package com.navercorp.openwhisk.intellij.explorer.dialog.trigger.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.service.WhiskService;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.utils.ParameterUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskTriggerService;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;

import javax.swing.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TriggerCreationManagerDialogForm {
    private static final Logger LOG = Logger.getInstance(TriggerCreationManagerDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JTextField triggerNameJTextField;
    private JPanel triggerNameJPanel;
    private JPanel triggerDefaultParameterJPanl;
    private JTextArea triggerDefaultParameterJTextArea;

    private Project project;
    private WhiskAuth auth;
    private WhiskTriggerService whiskTriggerService;

    public TriggerCreationManagerDialogForm(Project project, WhiskAuth auth) {
        this.project = project;
        this.auth = auth;
        this.whiskTriggerService = WhiskTriggerService.getInstance();
    }

    public void createTrigger() {
        try {
            // name
            String triggerName = triggerNameJTextField.getText().trim();
            // parameters
            Optional<String> params = ParameterUtils.validateParams(triggerDefaultParameterJTextArea.getText());
            if (!params.isPresent()) {
                NOTIFIER.notify(project, "The json format of the parameter is incorrect.", NotificationType.ERROR);
                return;
            }

            List<Map<String, Object>> validParams = ParameterUtils.mapToListMap(JsonParserUtils.parseMap(params.get()));
            // payload
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("name", triggerName);
            payload.put("parameters", validParams);
            whiskTriggerService.createWhiskTrigger(auth, triggerName, payload);

            refreshWhiskTree();
            NOTIFIER.notify(project, triggerName + " created", NotificationType.INFORMATION);
        } catch (IOException e) {
            String msg = "Failed to create trigger: " + triggerNameJTextField.getText().trim();
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }

    /**
     * Helper functions.
     */
    private void refreshWhiskTree() {
        EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
        WhiskService service = ServiceManager.getService(project, WhiskService.class);
        try {
            List<WhiskEndpoint> endpoints = JsonParserUtils.parseWhiskEndpoints(service.getEndpoints());
            if (!endpoints.isEmpty()) {
                EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
            } else {
                final String msg = "There are no endpoints saved.";
                LOG.info(msg);
                NOTIFIER.notify(project, msg, NotificationType.INFORMATION);
            }
        } catch (IOException e) {
            final String msg = "Entities cannot be loaded.";
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}
