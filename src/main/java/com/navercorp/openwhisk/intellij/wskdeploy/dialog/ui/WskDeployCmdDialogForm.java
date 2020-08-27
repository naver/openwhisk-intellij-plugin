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

package com.navercorp.openwhisk.intellij.wskdeploy.dialog.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.navercorp.openwhisk.intellij.common.service.WhiskService;
import com.navercorp.openwhisk.intellij.common.error.NotExistFileException;
import com.navercorp.openwhisk.intellij.common.utils.EventUtils;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.listener.RefreshWhiskTreeListener;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuthWithName;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.WskDeployCmd;
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.WskDeployCmdResponse;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.navercorp.openwhisk.intellij.common.utils.CommandUtils.runCommand;

public class WskDeployCmdDialogForm {
    private static final Logger LOG = Logger.getInstance(WskDeployCmdDialogForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JList namespaceJList;
    private JLabel selectMsgJabel;

    private Project project;
    private WskDeployCmd cmd;

    public WskDeployCmdDialogForm(Project project, WskDeployCmd cmd) {
        this.project = project;
        this.cmd = cmd;

        selectMsgJabel.setText("Please select the namespace you want to " + cmd.getCmdName() + ".");

        try {
            WhiskService whiskService = ServiceManager.getService(project, WhiskService.class);
            List<WhiskEndpoint> endpoints = new ArrayList<>(JsonParserUtils.parseWhiskEndpoints(whiskService.getEndpoints())); // make mutable

            namespaceJList.setListData(endpoints.stream().flatMap(ep ->
                    ep.getNamespaces().stream().map(ns ->
                            new WhiskAuthWithName(new WhiskAuth(ns.getAuth(), ep.getApihost()), ns.getPath()))).toArray());

        } catch (IOException e) {
            LOG.error("Endpoint parsing failed", e);
        }
    }

    public void runWskDeploy() {
        try {
            WhiskAuthWithName auth = (WhiskAuthWithName) namespaceJList.getSelectedValue();

            LOG.info(cmd.toCmdString());
            WskDeployCmdResponse response = runCommand(cmd.toCmd(auth.getAuth())); // TODO running background thread
            LOG.info(response.getSuccessOutput());
            String errMsg = response.getErrorOutput();
            if (!errMsg.isEmpty()) {
                LOG.error(errMsg);
            }

            if (response.getExistCode() == 0) {
                NOTIFIER.notify(project, response.getSuccessOutput(), NotificationType.INFORMATION);
                EventUtils.publish(project, RefreshWhiskTreeListener.TOPIC, RefreshWhiskTreeListener::refreshWhiskTree);
            } else {
                NOTIFIER.notify(project, response.getErrorOutput(), NotificationType.ERROR);
            }

        } catch (NotExistFileException | IOException | InterruptedException ex) {
            LOG.error(ex);
            NOTIFIER.notify(project, "Failed to running command(" + cmd.getCmdName() + ") for " + ex.getMessage(), NotificationType.ERROR);
        }
    }

    public JPanel getContent() {
        return mainJPanel;
    }
}
