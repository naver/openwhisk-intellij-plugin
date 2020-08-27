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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang.StringUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationWithLogs;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.CodeExec;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.CodeExecSerializer;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskRule;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;

import java.io.IOException;
import java.util.*;

public class JsonParserUtils {
    private static ObjectMapper mapper = new ObjectMapper();
    private static SimpleModule simpleModule = new SimpleModule();

    protected JsonParserUtils() {
        throw new UnsupportedOperationException("Utility classes should not have a public or default constructor.");
    }

    static {
        simpleModule.addSerializer(CodeExec.class, new CodeExecSerializer());
        mapper.registerModule(simpleModule);
    }

    public static List<WhiskActionMetaData> parseWhiskActions(String actions) throws IOException {
        if (StringUtils.isNotEmpty(actions)) {
            return Arrays.asList(mapper.readValue(actions, WhiskActionMetaData[].class));
        } else {
            return new ArrayList<>();
        }
    }

    public static Optional<ExecutableWhiskAction> parseWhiskAction(String action) throws IOException {
        if (StringUtils.isNotEmpty(action)) {
            return Optional.of(mapper.readValue(action, ExecutableWhiskAction.class));
        } else {
            return Optional.empty();
        }
    }

    public static List<WhiskPackage> parseWhiskPackages(String packages) throws IOException {
        if (StringUtils.isNotEmpty(packages)) {
            return Arrays.asList(mapper.readValue(packages, WhiskPackage[].class));
        } else {
            return new ArrayList<>();
        }
    }

    public static Optional<WhiskPackageWithActions> parseWhiskPackage(String pkg) throws IOException {
        if (StringUtils.isNotEmpty(pkg)) {
            return Optional.of(mapper.readValue(pkg, WhiskPackageWithActions.class));
        } else {
            return Optional.empty();
        }
    }

    // TODO test
    public static List<WhiskEndpoint> parseWhiskEndpoints(String endpoints) throws IOException {
        if (StringUtils.isNotEmpty(endpoints)) {
            return Arrays.asList(mapper.readValue(endpoints, WhiskEndpoint[].class));
        } else {
            return new ArrayList<>();
        }
    }

    // TODO test
    public static String writeEndpointsToJson(List<WhiskEndpoint> ep) throws JsonProcessingException {
        return mapper.writeValueAsString(ep);
    }

    // TODO test
    public static String[] parseWhiskNamespace(String namespaces) throws IOException {
        if (StringUtils.isNotEmpty(namespaces)) {
            return mapper.readValue(namespaces, String[].class);
        } else {
            return new String[]{};
        }
    }

    // TODO test
    public static List<WhiskActivationMetaData> parseWhiskActivations(String actions) throws IOException {
        if (StringUtils.isNotEmpty(actions)) {
            return Arrays.asList(mapper.readValue(actions, WhiskActivationMetaData[].class));
        } else {
            return new ArrayList<>();
        }
    }

    // TODO test
    public static Optional<WhiskActivationWithLogs> parseWhiskActivation(String actions) throws IOException {
        if (StringUtils.isNotEmpty(actions)) {
            return Optional.of(mapper.readValue(actions, WhiskActivationWithLogs.class));
        } else {
            return Optional.empty();
        }
    }

    // TODO test
    public static String writeWhiskActivationToJson(WhiskActivationWithLogs activation) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(activation);
    }

    public static List<WhiskTriggerMetaData> parseWhiskTriggers(String triggers) throws IOException {
        if (StringUtils.isNotEmpty(triggers)) {
            return Arrays.asList(mapper.readValue(triggers, WhiskTriggerMetaData[].class));
        } else {
            return new ArrayList<>();
        }
    }

    public static Optional<ExecutableWhiskTrigger> parseWhiskTrigger(String triggers) throws IOException {
        if (StringUtils.isNotEmpty(triggers)) {
            return Optional.of(mapper.readValue(triggers, ExecutableWhiskTrigger.class));
        } else {
            return Optional.empty();
        }
    }

    // TODO test
    public static Optional<WhiskRule> parseWhiskRule(String rule) throws IOException {
        if (StringUtils.isNotEmpty(rule)) {
            return Optional.of(mapper.readValue(rule, WhiskRule.class));
        } else {
            return Optional.empty();
        }
    }

    // TODO test
    public static String writeParameterToJson(List<Map<String, Object>> params) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ParameterUtils.listMapToMap(params));
    }

    // TODO test
    public static List<Map<String, Object>> parseListMap(String json) throws IOException {
        if (StringUtils.isNotEmpty(json)) {
            return mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } else {
            return new ArrayList<>();
        }
    }

    // TODO test
    public static String writeListMapToJson(List<Map<String, Object>> maps) throws JsonProcessingException {
        return mapper.writer().writeValueAsString(maps);
    }

    // TODO test
    public static Map<String, Object> parseMap(String json) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return mapper.readValue(json, Map.class);
            } catch (IOException e) {
                return new LinkedHashMap<>();
            }
        } else {
            return new LinkedHashMap<>();
        }
    }

    // TODO test
    public static String writeMapToJson(Map<String, Object> map) throws JsonProcessingException {
        return mapper.writeValueAsString(map);
    }

    // TODO test
    public static boolean isValidJson(final String json) throws IOException {
        boolean valid = true;
        try {
            mapper.readTree(json);
        } catch (JsonProcessingException e) {
            valid = false;
        }
        return valid;
    }

    // TODO test
    public static String beautifyJson(String json) throws IOException {
        if (StringUtils.isNotEmpty(json)) {
            Map<String, Object> j = mapper.readValue(json, Map.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(j);
        } else {
            return "";
        }
    }
}
