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

package org.apache.openwhisk.intellij.common;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import gnu.trove.THashMap;

import javax.swing.*;
import java.util.Map;

public class Icons {

    private static final Map<String, Icon> REGISTERED_ICONS = new THashMap<>();

    public static final Icon ENTITY_TRIGGER = load("/icons/event.svg");
    public static final Icon ENTITY_TRIGGER_ROOT = load("/icons/eventGroup.svg");

    public static final Icon KIND_JAVA = load("/icons/language/java/java.svg");
    public static final Icon KIND_JS = load("/icons/language/javascript/javascript.svg");
    public static final Icon KIND_GO = load("/icons/language/go/go.svg");
    public static final Icon KIND_PYTHON = load("/icons/language/python/py.svg");
    public static final Icon KIND_PHP = load("/icons/language/php/php.svg");
    public static final Icon KIND_RUBY = load("/icons/language/ruby/rb.svg");
    public static final Icon KIND_SWIFT = load("/icons/language/swift/sw.svg");
    public static final Icon KIND_SEQUENCE = load("/icons/seq.svg");
    public static final Icon KIND_DOCKER = load("/icons/language/docker/DockerCompose.svg");

    private static Icon load(String path) {
        try {
            return IconLoader.getIcon(path);
        } catch (Throwable t) {
            return AllIcons.General.Warning;
        }
    }

    private static Icon load(String key, String path) {
        Icon icon = load(path);
        REGISTERED_ICONS.put(key, icon);
        return icon;
    }

    public static Icon getIcon(String key) {
        return REGISTERED_ICONS.get(key);
    }

}
