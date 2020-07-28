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

package com.navercorp.openwhisk.intellij.common.utils.json;

import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.SimplifiedEntityMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.SimplifiedWhiskRule;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class JsonParserUtilsTests {

    /**
     * Helper functions
     */
    private List<Map<String, Object>> createTriggerAlarmFeedAnnotation() {
        List<Map<String, Object>> annotations = new ArrayList<>();

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("key", "feed");
        entry.put("value", "/whisk.system/alarms/alarm");
        annotations.add(entry);
        return annotations;
    }

    private Map<String, SimplifiedWhiskRule> createRule() {
        Map<String, SimplifiedWhiskRule> rules = new LinkedHashMap<>();
        rules.put("test/rule1", new SimplifiedWhiskRule(new SimplifiedEntityMetaData("test", "action1"), "active"));
        return rules;
    }

    @Test
    public void parseWhiskTriggers() throws IOException {
        String triggers = "[\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"feed\",\n" +
                "        \"value\": \"/whisk.system/alarms/alarm\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"trigger1\",\n" +
                "    \"namespace\": \"test\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1586315510331,\n" +
                "    \"version\": \"0.0.2\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"feed\",\n" +
                "        \"value\": \"/whisk.system/alarms/alarm\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"trigger2\",\n" +
                "    \"namespace\": \"test\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1586315245020,\n" +
                "    \"version\": \"0.0.2\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [],\n" +
                "    \"name\": \"trigger3\",\n" +
                "    \"namespace\": \"test\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1582612773239,\n" +
                "    \"version\": \"0.0.2\"\n" +
                "  }\n" +
                "]]";

        List<WhiskTriggerMetaData> expected = new ArrayList<>();
        expected.add(new WhiskTriggerMetaData("trigger1", "test", "0.0.2", 1586315510331L, false, createTriggerAlarmFeedAnnotation()));
        expected.add(new WhiskTriggerMetaData("trigger2", "test", "0.0.2", 1586315245020L, false, createTriggerAlarmFeedAnnotation()));
        expected.add(new WhiskTriggerMetaData("trigger3", "test", "0.0.2", 1582612773239L, false, new ArrayList<>()));

        // when
        List<WhiskTriggerMetaData> actual = JsonParserUtils.parseWhiskTriggers(triggers);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseWhiskTrigger() throws IOException {
        String trigger = "{\n" +
                "  \"annotations\": [\n" +
                "    {\n" +
                "      \"key\": \"feed\",\n" +
                "      \"value\": \"/whisk.system/alarms/alarm\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"limits\": {},\n" +
                "  \"name\": \"trigger1\",\n" +
                "  \"namespace\": \"test\",\n" +
                "  \"parameters\": [],\n" +
                "  \"publish\": false,\n" +
                "  \"rules\": {\n" +
                "    \"test/rule1\": {\n" +
                "      \"action\": {\n" +
                "        \"name\": \"action1\",\n" +
                "        \"path\": \"test\"\n" +
                "      },\n" +
                "      \"status\": \"active\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"updated\": 1586315245020,\n" +
                "  \"version\": \"0.0.2\"\n" +
                "}";

        ExecutableWhiskTrigger expected = new ExecutableWhiskTrigger("trigger1", "test", "0.0.2", 1586315245020L, false, createTriggerAlarmFeedAnnotation(), new ArrayList<>(), createRule(), new LinkedHashMap<>());

        // when
        ExecutableWhiskTrigger actual = JsonParserUtils.parseWhiskTrigger(trigger).get();

        // then
        assertEquals(actual, expected);
    }

}
