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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnotationHelper {

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
}
