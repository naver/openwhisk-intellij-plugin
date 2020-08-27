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

package com.navercorp.openwhisk.intellij.explorer.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import com.navercorp.openwhisk.intellij.common.utils.WhiskUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActivationService;
import com.navercorp.openwhisk.intellij.explorer.editor.model.ComboBoxEntityEntry;
import com.navercorp.openwhisk.intellij.explorer.editor.ui.ActivationViewEditorForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActivationViewEditor implements FileEditor {
    private static final Logger LOG = Logger.getInstance(ActivationViewEditor.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private Project project;
    private ActivationViewEditorForm activationViewEditorForm;
    private WhiskActivationService whiskActivationService = WhiskActivationService.getInstance();

    public ActivationViewEditor(Project project,
                                List<WhiskEndpoint> endpoints,
                                Optional<WhiskAuth> whiskAuth,
                                Optional<WhiskActionMetaData> action,
                                Optional<WhiskTriggerMetaData> trigger) {
        activationViewEditorForm = new ActivationViewEditorForm(project, endpoints);
        activationViewEditorForm.cacheWhiskAuth(whiskAuth);

        /**
         * Initialize ComboBox values
         */
        List<WhiskNamespace> namespaces = endpoints.stream().flatMap(ep -> ep.getNamespaces().stream()).collect(Collectors.toList());
        activationViewEditorForm.initializeNamespaceJComboBox(namespaces);

        whiskAuth.ifPresent(auth ->
                getEntity(action, trigger).ifPresent(entity -> {

                    /**
                     * Load Activations
                     *
                     * Note: Activations of binding package action cannot be loaded.
                     * TODO load activations of binding action after the follow upstream pr is merged: https://github.com/apache/openwhisk/pull/4919
                     */
                    try {
                        List<WhiskActivationMetaData> activations =
                                whiskActivationService.getWhiskActivations(auth, entity.toEntityName(), 100, 0); // TODO pagination
                        activationViewEditorForm.initializeActivationTable(activations);
                    } catch (IOException e) {
                        LOG.error(e);
                    }

                    /**
                     * Set value to namespace combobox
                     */
                    WhiskUtils.findWhiskNamespace(endpoints, auth.getAuth())
                            .ifPresent(namespace -> activationViewEditorForm.setNamespaceJComboBox(namespace));

                    /**
                     * Set value to action or trigger combobox
                     */
                    activationViewEditorForm.setActionOrTriggerJComboBox(entity);
                }));
    }

    private Optional<ComboBoxEntityEntry> getEntity(Optional<WhiskActionMetaData> action, Optional<WhiskTriggerMetaData> trigger) {
        if (action.isPresent() && trigger.isPresent()) {
            return Optional.empty();
        } else if (action.isPresent()) {
            return action.map(WhiskActionMetaData::toCombBoxEntityEntry);
        } else if (trigger.isPresent()) {
            return trigger.map(WhiskTriggerMetaData::toCombBoxEntityEntry);
        } else {
            return Optional.of(ComboBoxEntityEntry.NONE_COMBO_BOX_ENTITY_ENTRY);
        }
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return activationViewEditorForm.getContent();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return "activation editor";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }

    @Override
    public void selectNotify() {

    }

    @Override
    public void deselectNotify() {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null; // Return {@code null} if no background highlighting activity necessary for this file editor.
    }
}
