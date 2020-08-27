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

package com.navercorp.openwhisk.intellij.explorer.editor.model;

import java.util.Objects;
import java.util.Optional;

public class ComboBoxEntityEntry {
    public static final ComboBoxEntityEntry NONE_COMBO_BOX_ENTITY_ENTRY = new ComboBoxEntityEntry("None (Please select an entity)", ComboBoxEntityType.NONE);

    private String name;
    private ComboBoxEntityType type;

    public ComboBoxEntityEntry() {
    }

    public ComboBoxEntityEntry(String name, ComboBoxEntityType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ComboBoxEntityType getType() {
        return type;
    }

    public void setType(ComboBoxEntityType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComboBoxEntityEntry that = (ComboBoxEntityEntry) o;
        return Objects.equals(name, that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    public Optional<String> toEntityName() {
        switch (type) {
            case ACTION:
            case TRIGGER:
                return Optional.of(name);
            case NONE:
            default:
                return Optional.empty();
        }
    }
}
