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

package com.navercorp.openwhisk.intellij.utils;

import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.SimplifiedEntityMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.SimplifiedWhiskRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnotationHelper {

    protected AnnotationHelper() {
        throw new UnsupportedOperationException("Utility classes should not have a public or default constructor.");
    }

    public static List<Map<String, Object>> createActionAnnotation(boolean webExport, boolean rawHttp, boolean customOption, boolean finalEntry, String kind) {
        List<Map<String, Object>> annotations = new ArrayList<>();
        if (webExport) {
            Map<String, Object> entry1 = new LinkedHashMap<>();
            entry1.put("key", "web-export");
            entry1.put("value", webExport);
            annotations.add(entry1);

            Map<String, Object> entry2 = new LinkedHashMap<>();
            entry2.put("key", "raw-http");
            entry2.put("value", rawHttp);
            annotations.add(entry2);

            if (customOption) {
                Map<String, Object> entry3 = new LinkedHashMap<>();
                entry3.put("key", "web-custom-options");
                entry3.put("value", customOption);
                annotations.add(entry3);
            }

            Map<String, Object> entry4 = new LinkedHashMap<>();
            entry4.put("key", "final");
            entry4.put("value", finalEntry);
            annotations.add(entry4);
        }

        Map<String, Object> entry5 = new LinkedHashMap<>();
        entry5.put("key", "exec");
        entry5.put("value", kind);
        annotations.add(entry5);

        return annotations;
    }

    public static List<Map<String, Object>> createPackageAnnotation(String description, Map<String, String> binding) {
        List<Map<String, Object>> annotations = new ArrayList<>();

        Map<String, Object> entry1 = new LinkedHashMap<>();
        entry1.put("key", "description");
        entry1.put("value", description);
        annotations.add(entry1);

        if (binding != null) {
            Map<String, Object> entry2 = new LinkedHashMap<>();
            entry2.put("key", "binding");
            entry2.put("value", binding);
            annotations.add(entry2);
        }

        return annotations;
    }

    public static Map<String, String> createBinding(String name, String namespace) {
        Map<String, String> binding = new LinkedHashMap<>();
        binding.put("name", name);
        binding.put("namespace", namespace);
        return binding;
    }

    public static Map<String, String> createEmptyBinding() {
        return new LinkedHashMap<>();
    }

    public static boolean createFalseBinding() {
        return false;
    }

    public static List<Map<String, Object>> createTriggerAlarmFeedAnnotation() {
        List<Map<String, Object>> annotations = new ArrayList<>();

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("key", "feed");
        entry.put("value", "/whisk.system/alarms/alarm");
        annotations.add(entry);
        return annotations;
    }

    public static Map<String, SimplifiedWhiskRule> createRule(String ruleName, String namespace, String actionName) {
        Map<String, SimplifiedWhiskRule> rules = new LinkedHashMap<>();
        rules.put(namespace + "/" + ruleName, new SimplifiedWhiskRule(new SimplifiedEntityMetaData(namespace, actionName), "active"));
        return rules;
    }
}
