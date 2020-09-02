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

package com.navercorp.openwhisk.intellij.explorer.dialog.action.ui;

import com.navercorp.openwhisk.intellij.common.whisk.model.Runtime;

import javax.swing.*;
import javax.swing.event.ListDataListener;

public class CodeTypeForm {
    private JPanel mainJPanel;
    private JComboBox codeTypeJComboBox;

    public CodeTypeForm() {

        codeTypeJComboBox.setModel(new ComboBoxModel<Runtime>() {
            private Runtime selected;

            @Override
            public void setSelectedItem(Object anItem) {
                selected = (Runtime) anItem;
            }

            @Override
            public Object getSelectedItem() {
                return this.selected;
            }

            @Override
            public int getSize() {
                // remove blackbox, sequence
                return Runtime.values().length - 2;
            }

            @Override
            public Runtime getElementAt(int index) {
                switch (index) {
                    case 0:
                        return Runtime.NODE_6;
                    case 1:
                        return Runtime.NODE_8;
                    case 2:
                        return Runtime.NODE_10;
                    case 3:
                        return Runtime.NODE_12;
                    case 4:
                        return Runtime.PYTHON_2;
                    case 5:
                        return Runtime.PYTHON_3;
                    case 6:
                        return Runtime.JAVA;
                    case 7:
                        return Runtime.SWIFT_3_1_1;
                    case 8:
                        return Runtime.SWIFT_4_2;
                    case 9:
                        return Runtime.PHP_7_2;
                    case 10:
                        return Runtime.GO_1_11;
                    default:
                        return Runtime.RUBY_2_5;
                }
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

    public void setCodeType(String codeType) {
        Runtime runtime = Runtime.toRuntime(codeType);
        switch (runtime) {
            case SEQUENCE:
            case DOCKER:
                break;
            default: // normal
                codeTypeJComboBox.setSelectedItem(runtime);
        }
    }

    public Runtime getSelectedCodeType() {
        return (Runtime) codeTypeJComboBox.getSelectedItem();
    }

    public JPanel getContent() {
        return mainJPanel;
    }

}
