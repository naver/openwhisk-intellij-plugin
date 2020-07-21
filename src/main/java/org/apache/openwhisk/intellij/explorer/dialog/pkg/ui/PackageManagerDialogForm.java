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

package org.apache.openwhisk.intellij.explorer.dialog.pkg.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import org.apache.commons.lang.StringUtils;
import org.apache.openwhisk.intellij.common.utils.JsonParserUtils;
import org.apache.openwhisk.intellij.common.notification.SimpleNotifier;
import org.apache.openwhisk.intellij.common.whisk.model.Binding;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskAuth;
import org.apache.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import org.apache.openwhisk.intellij.common.whisk.service.WhiskPackageService;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.intellij.uiDesigner.core.GridConstraints.*;
import static org.apache.openwhisk.intellij.common.utils.JsonParserUtils.parseMap;
import static org.apache.openwhisk.intellij.common.utils.ParameterUtils.mapToListMap;

public class PackageManagerDialogForm {
    private final static Logger LOG = Logger.getInstance(PackageManagerDialogForm.class);
    private final static SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private JPanel mainJPanel;
    private JCheckBox shareJCheckBox;
    private JLabel nameJLabel;
    private JLabel namespaceJPanel;
    private JLabel versionJLabel;
    private JTextArea defaultParameterTextArea;
    private JPanel sharedJPanel;

    private WhiskPackageService whiskPackageService = WhiskPackageService.getInstance();

    private Project project;
    private WhiskAuth whiskAuth;
    private WhiskPackageWithActions whiskPackage;

    public PackageManagerDialogForm(Project project, WhiskAuth whiskAuth, WhiskPackageWithActions whiskPackage) {
        this.project = project;
        this.whiskAuth = whiskAuth;
        this.whiskPackage = whiskPackage;

        nameJLabel.setText(whiskPackage.getName());
        namespaceJPanel.setText(whiskPackage.getNamespace());
        versionJLabel.setText(whiskPackage.getVersion());

        Optional<Binding> binding = whiskPackage.getBinding();
        if (binding.isPresent()) {
            sharedJPanel.remove(shareJCheckBox);
            Binding b = binding.get();
            JLabel label = new JLabel("from " + b.getNamespace() + "/" + b.getName());
            label.setPreferredSize(new Dimension(250, -1));
            sharedJPanel.add(label, new GridConstraints(0, 2, 1, 1, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, SIZEPOLICY_CAN_GROW | SIZEPOLICY_CAN_SHRINK, FILL_NONE, ANCHOR_CENTER, new Dimension(-1, -1), new Dimension(250, -1), new Dimension(-1, -1), 0));
        } else {
            shareJCheckBox.setSelected(whiskPackage.isPublish());
        }

        try {
            defaultParameterTextArea.setText(JsonParserUtils.writeParameterToJson(whiskPackage.getParameters()));
        } catch (JsonProcessingException e) {
            LOG.error("Failed to parse json: " + whiskPackage.getName(), e);
        }
    }

    public void updatePackage() {
        try {
            /**
             * Update default parameters
             */
            // parameters
            List<Map<String, Object>> params = parametersToCollection(whiskPackage, defaultParameterTextArea.getText());
            // payload
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("name", whiskPackage.getName());
            payload.put("namespace", whiskPackage.getNamespace());
            payload.put("parameters", params);
            if (shareJCheckBox.isValid()) {
                payload.put("publish", shareJCheckBox.isSelected());
            }

            whiskPackageService.updateWhiskPackage(whiskAuth, whiskPackage.getName(), payload)
                    .ifPresent(updated -> NOTIFIER.notify(project, updated.getName() + " updated", NotificationType.INFORMATION));
        } catch (IOException e) {
            String msg = "Failed to update package: " + whiskPackage.getName();
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }

    }

    private boolean validateParams(String params) throws IOException {
        return JsonParserUtils.isValidJson(params);
    }

    private List<Map<String, Object>> parametersToCollection(WhiskPackageWithActions pkg, String param) throws IOException {
        if (StringUtils.isEmpty(param) || !validateParams(param)) {
            return pkg.getParameters();
        }
        return mapToListMap(parseMap(param));
    }

    public JPanel getContent() {
        return mainJPanel;
    }
}
