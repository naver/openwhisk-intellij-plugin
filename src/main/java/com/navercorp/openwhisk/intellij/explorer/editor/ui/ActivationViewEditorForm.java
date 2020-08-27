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

package com.navercorp.openwhisk.intellij.explorer.editor.ui;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.navercorp.openwhisk.intellij.common.utils.FileUtils;
import com.navercorp.openwhisk.intellij.common.utils.WhiskUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationWithLogs;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskActivationService;
import com.navercorp.openwhisk.intellij.explorer.editor.model.ComboBoxEntityEntry;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivationViewEditorForm {
    private static final Logger LOG = Logger.getInstance(ActivationViewEditorForm.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private static final int ACTIVATION_ID_COLUMN = 1;

    private JPanel mainJPanel;
    private JPanel actionsToobarJPanel;
    private JTable activationsJTable;
    private JComboBox namespaceJComboBox;
    private JComboBox actionOrTriggerJComboBox;
    private JButton searchJButton;

    private WhiskActivationService whiskActivationService = WhiskActivationService.getInstance();
    private FileEditorManager fileEditorManager;

    private DefaultTableModel activationsTableModel;

    private Optional<WhiskAuth> currentAuth = Optional.empty();

    public ActivationViewEditorForm(Project project, List<WhiskEndpoint> endpoints) {
        this.fileEditorManager = FileEditorManager.getInstance(project);

        activationsTableModel = new DefaultTableModel(initActivationColumnModel(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Activation ID(1) & Entity(6) are editable to copy value
                return column == 1 || column == 6;
            }
        };

        activationsJTable.setModel(activationsTableModel);
        activationsJTable.setDragEnabled(true);
        activationsJTable.setAutoCreateRowSorter(true);

        activationsJTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1 && currentAuth.isPresent()) {
                    int row = activationsJTable.rowAtPoint(e.getPoint());
                    String activationId = (String) activationsTableModel.getValueAt(row, ACTIVATION_ID_COLUMN);
                    try {
                        whiskActivationService.getWhiskActivation(currentAuth.get(), activationId)
                                .ifPresent(activationWithLogs -> openActivationEditor(project, activationWithLogs));
                    } catch (IOException ex) {
                        LOG.error("Failed to fetch activations: " + activationId, ex);
                    }
                }
            }
        });

        searchJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WhiskNamespace whiskNamespace = (WhiskNamespace) namespaceJComboBox.getSelectedItem();
                ComboBoxEntityEntry entity = (ComboBoxEntityEntry) actionOrTriggerJComboBox.getSelectedItem();
                WhiskUtils.findWhiskAuth(endpoints, whiskNamespace).ifPresent(auth ->
                        initializeActivationTable(loadActivations(auth, Optional.ofNullable(entity))));
            }
        });
    }

    public void initializeNamespaceJComboBox(List<WhiskNamespace> namespaces) {
        namespaceJComboBox.setModel(new ComboBoxModel<WhiskNamespace>() {
            private WhiskNamespace namespace;

            @Override
            public void setSelectedItem(Object anItem) {
                namespace = (WhiskNamespace) anItem;
            }

            @Override
            public WhiskNamespace getSelectedItem() {
                return this.namespace;
            }

            @Override
            public int getSize() {
                return namespaces.size();
            }

            @Override
            public WhiskNamespace getElementAt(int index) {
                return namespaces.get(index);
            }

            @Override
            public void addListDataListener(ListDataListener l) {
                // nothing to do
            }

            @Override
            public void removeListDataListener(ListDataListener l) {
                // nothing to do
            }
        });

        namespaceJComboBox.addActionListener(e -> {
            WhiskNamespace namespace = (WhiskNamespace) namespaceJComboBox.getSelectedItem();
            initializeActionAndTriggerJComboBox(namespace.getActions(), namespace.getTriggers());
        });
    }

    public void initializeActionAndTriggerJComboBox(List<WhiskActionMetaData> actions, List<WhiskTriggerMetaData> triggers) {
        /**
         * It shows the list of none, action, trigger in one JComboBox at once. The order is as follows: None ~ Actions ~ triggers
         *
         * TODO Add binding action
         */
        List<ComboBoxEntityEntry> entries = Stream.of(
                Stream.of(ComboBoxEntityEntry.NONE_COMBO_BOX_ENTITY_ENTRY),
                actions.stream().map(WhiskActionMetaData::toCombBoxEntityEntry),
                triggers.stream().map(WhiskTriggerMetaData::toCombBoxEntityEntry))
                .flatMap(a -> a)
                .collect(Collectors.toList());

        actionOrTriggerJComboBox.setModel(new ComboBoxModel<ComboBoxEntityEntry>() {
            private ComboBoxEntityEntry selected;

            @Override
            public void setSelectedItem(Object anItem) {
                this.selected = (ComboBoxEntityEntry) anItem;
            }

            @Override
            public ComboBoxEntityEntry getSelectedItem() {
                return selected;
            }

            @Override
            public int getSize() {
                return entries.size();
            }

            @Override
            public ComboBoxEntityEntry getElementAt(int index) {
                return entries.get(index);
            }

            @Override
            public void addListDataListener(ListDataListener l) {
                // nothing to do
            }

            @Override
            public void removeListDataListener(ListDataListener l) {
                // nothing to do
            }
        });
    }

    public void cacheWhiskAuth(Optional<WhiskAuth> whiskAuth) {
        this.currentAuth = whiskAuth;
    }

    public void initializeActivationTable(List<WhiskActivationMetaData> activations) {
        clearActivationTable();
        for (WhiskActivationMetaData activation : activations) {
            activationsTableModel.addRow(createRow(activation));
        }
    }

    public void setNamespaceJComboBox(WhiskNamespace namespace) {
        namespaceJComboBox.setSelectedItem(namespace);
        initializeActionAndTriggerJComboBox(namespace.getActions(), namespace.getTriggers());
    }

    public void setActionOrTriggerJComboBox(ComboBoxEntityEntry entity) {
        actionOrTriggerJComboBox.setSelectedItem(entity);
    }

    /**
     * Helper functions.
     */
    private List<WhiskActivationMetaData> loadActivations(WhiskAuth auth, Optional<ComboBoxEntityEntry> entity) {
        try {
            return whiskActivationService.getWhiskActivations(auth, entity.flatMap(ComboBoxEntityEntry::toEntityName), 100, 0); // TODO pagination
        } catch (IOException e) {
            LOG.error(e);
            return new ArrayList<>();
        }
    }

    private void openActivationEditor(Project project, WhiskActivationWithLogs activationWithLogs) {
        try {
            final String tmpFilePath = project.getBasePath() + "/.idea/openwhisk";
            VirtualFile activationFile = FileUtils.writeActivationToFile(tmpFilePath, activationWithLogs);
            fileEditorManager.openFile(activationFile, true);
        } catch (IOException e) {
            LOG.error("Failed to open file: " + activationWithLogs.getName(), e);
        }
    }

    private void clearActivationTable() {
        activationsTableModel.setRowCount(0);
    }

    private Vector<String> initActivationColumnModel() {
        Vector<String> columnModel = new Vector<>();
        columnModel.add("Datetime");
        columnModel.add("Activation ID");
        columnModel.add("Kind");
        columnModel.add("Start");
        columnModel.add("Duration");
        columnModel.add("Status");
        columnModel.add("Entity");
        return columnModel;
    }

    private Vector<String> createRow(WhiskActivationMetaData activation) {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(activation.getStart()), ZoneId.systemDefault());
        Vector<String> rowModel = new Vector<>();
        rowModel.add(dateFormat.format(ldt));           // Datetime
        rowModel.add(activation.getActivationId());     // Activation Id
        rowModel.add(activation.getKind());             // Kind
        rowModel.add(activation.getStartType());        // Start
        rowModel.add(activation.getDuration() + "ms");  // Duration
        rowModel.add(activation.getStatus());           // Status
        rowModel.add(activation.getNamespace() + "/" + activation.getName() + ":" + activation.getVersion()); // Entity
        return rowModel;
    }

    public JPanel getContent() {
        return mainJPanel;
    }
}
