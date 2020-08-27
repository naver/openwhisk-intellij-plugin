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

package com.navercorp.openwhisk.intellij.common.utils;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.*;

public class ParameterUtils {

    protected ParameterUtils() {
        throw new UnsupportedOperationException("Utility classes should not have a public or default constructor.");
    }

    // TODO test
    public static Map<String, Object> listMapToMap(List<Map<String, Object>> params) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map<String, Object> p : params) {
            map.put((String) p.get("key"), p.get("value"));
        }
        return map;
    }

    public static List<Map<String, Object>> mapToListMap(Map<String, Object> params) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String key : params.keySet()) {
            Object v = params.get(key);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("key", key);
            m.put("value", v);
            list.add(m);
        }
        return list;
    }

    public static Optional<String> validateParams(String params) {
        if (StringUtils.isEmpty(params)) {
            return Optional.of("{}");
        } else {
            try {
                if (JsonParserUtils.isValidJson(params)) {
                    return Optional.of(params);
                } else {
                    return Optional.empty();
                }
            } catch (IOException e) {
                return Optional.empty();
            }
        }
    }
}
