package org.apache.openwhisk.intellij.common.utils.json; /**
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

import org.apache.openwhisk.intellij.common.utils.JsonParserUtils;
import org.apache.openwhisk.intellij.common.whisk.model.Limits;
import org.apache.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;
import org.apache.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import org.apache.openwhisk.intellij.common.whisk.model.action.WhiskAction;
import org.apache.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import org.apache.openwhisk.intellij.common.whisk.model.exec.CodeExec;
import org.apache.openwhisk.intellij.common.whisk.model.exec.ExecMetaData;
import org.apache.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import org.apache.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import org.apache.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import org.apache.openwhisk.intellij.common.whisk.model.trigger.SimplifiedEntityMetaData;
import org.apache.openwhisk.intellij.common.whisk.model.trigger.SimplifiedWhiskRule;
import org.apache.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
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
    private List<Map<String, Object>> createActionAnnotation(boolean webExport, boolean rawHttp, boolean finalEntry, String exec) {
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
        }

        if (finalEntry) {
            Map<String, Object> entry3 = new LinkedHashMap<>();
            entry3.put("key", "final");
            entry3.put("value", finalEntry);
            annotations.add(entry3);
        }

        Map<String, Object> entry4 = new LinkedHashMap<>();
        entry4.put("key", "exec");
        entry4.put("value", exec);
        annotations.add(entry4);

        return annotations;
    }

    private List<Map<String, Object>> createPackageAnnotation(Map<String, String> binding) {
        List<Map<String, Object>> annotations = new ArrayList<>();

        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("key", "description");
        entry.put("value", "test");
        annotations.add(entry);

        if (binding != null) {
            Map<String, Object> entry1 = new LinkedHashMap<>();
            entry1.put("key", "binding");
            entry1.put("value", binding);
            annotations.add(entry1);
        }

        return annotations;
    }

    private Map<String, String> createBinding(String name, String namespace) {
        Map<String, String> binding = new LinkedHashMap<>();
        binding.put("name", name);
        binding.put("namespace", namespace);
        return binding;
    }

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
    public void parseWhiskActions() throws IOException {

        // given
        String actions = "[\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"web-export\",\n" +
                "        \"value\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"raw-http\",\n" +
                "        \"value\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"final\",\n" +
                "        \"value\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"exec\",\n" +
                "        \"value\": \"nodejs:10\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"exec\": {\n" +
                "      \"binary\": true\n" +
                "    },\n" +
                "    \"limits\": {\n" +
                "      \"concurrency\": 1,\n" +
                "      \"logs\": 1,\n" +
                "      \"memory\": 256,\n" +
                "      \"timeout\": 60000\n" +
                "    },\n" +
                "    \"name\": \"testAct1\",\n" +
                "    \"namespace\": \"testNs/testPkg1\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1585756253499,\n" +
                "    \"version\": \"0.0.1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"web-export\",\n" +
                "        \"value\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"raw-http\",\n" +
                "        \"value\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"final\",\n" +
                "        \"value\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"exec\",\n" +
                "        \"value\": \"nodejs:10\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"exec\": {\n" +
                "      \"binary\": true\n" +
                "    },\n" +
                "    \"limits\": {\n" +
                "      \"concurrency\": 1,\n" +
                "      \"logs\": 1,\n" +
                "      \"memory\": 256,\n" +
                "      \"timeout\": 60000\n" +
                "    },\n" +
                "    \"name\": \"testAct2\",\n" +
                "    \"namespace\": \"testNs/testPkg2\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1585756252393,\n" +
                "    \"version\": \"0.0.1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"web-export\",\n" +
                "        \"value\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"raw-http\",\n" +
                "        \"value\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"final\",\n" +
                "        \"value\": true\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"exec\",\n" +
                "        \"value\": \"nodejs:10\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"exec\": {\n" +
                "      \"binary\": true\n" +
                "    },\n" +
                "    \"limits\": {\n" +
                "      \"concurrency\": 1,\n" +
                "      \"logs\": 1,\n" +
                "      \"memory\": 256,\n" +
                "      \"timeout\": 60000\n" +
                "    },\n" +
                "    \"name\": \"testAct3\",\n" +
                "    \"namespace\": \"testNs/testPkg3\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1585756251289,\n" +
                "    \"version\": \"0.0.1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"exec\",\n" +
                "        \"value\": \"shell\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"exec\": {\n" +
                "      \"binary\": false\n" +
                "    },\n" +
                "    \"limits\": {\n" +
                "      \"concurrency\": 1,\n" +
                "      \"logs\": 1,\n" +
                "      \"memory\": 256,\n" +
                "      \"timeout\": 60000\n" +
                "    },\n" +
                "    \"name\": \"testAct4\",\n" +
                "    \"namespace\": \"testNs/testPkg4\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1582709807073,\n" +
                "    \"version\": \"0.0.2\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"exec\",\n" +
                "        \"value\": \"python:3\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"exec\": {\n" +
                "      \"binary\": false\n" +
                "    },\n" +
                "    \"limits\": {\n" +
                "      \"concurrency\": 1,\n" +
                "      \"logs\": 1,\n" +
                "      \"memory\": 256,\n" +
                "      \"timeout\": 60000\n" +
                "    },\n" +
                "    \"name\": \"testAct5\",\n" +
                "    \"namespace\": \"testNs/testPkg5\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1581316522182,\n" +
                "    \"version\": \"0.0.1\"\n" +
                "  }\n" +
                "]";

        List<WhiskAction> expected = new ArrayList<>();
        expected.add(new WhiskActionMetaData("testAct1", "testNs/testPkg1", "0.0.1", 1585756253499L, false, createActionAnnotation(true, false, true, "nodejs:10"), new Limits(1, 1, 256, 60000), new ExecMetaData(true)));
        expected.add(new WhiskActionMetaData("testAct2", "testNs/testPkg2", "0.0.1", 1585756252393L, false, createActionAnnotation(true, false, true, "nodejs:10"), new Limits(1, 1, 256, 60000), new ExecMetaData(true)));
        expected.add(new WhiskActionMetaData("testAct3", "testNs/testPkg3", "0.0.1", 1585756251289L, false, createActionAnnotation(true, false, true, "nodejs:10"), new Limits(1, 1, 256, 60000), new ExecMetaData(true)));
        expected.add(new WhiskActionMetaData("testAct4", "testNs/testPkg4", "0.0.2", 1582709807073L, false, createActionAnnotation(false, false, false, "shell"), new Limits(1, 1, 256, 60000), new ExecMetaData(false)));
        expected.add(new WhiskActionMetaData("testAct5", "testNs/testPkg5", "0.0.1", 1581316522182L, false, createActionAnnotation(false, false, false, "python:3"), new Limits(1, 1, 256, 60000), new ExecMetaData(false)));

        // when
        List<WhiskActionMetaData> actual = JsonParserUtils.parseWhiskActions(actions);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseWhiskAction() throws IOException {
        String action = "{\n" +
                "  \"annotations\": [\n" +
                "    {\n" +
                "      \"key\": \"exec\",\n" +
                "      \"value\": \"nodejs:10\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"exec\": {\n" +
                "    \"kind\": \"nodejs:10\",\n" +
                "    \"code\": \"function main(params) {}\",\n" +
                "    \"binary\": false\n" +
                "  },\n" +
                "  \"limits\": {\n" +
                "    \"concurrency\": 1,\n" +
                "    \"logs\": 1,\n" +
                "    \"memory\": 256,\n" +
                "    \"timeout\": 60000\n" +
                "  },\n" +
                "  \"name\": \"testAct\",\n" +
                "  \"namespace\": \"testNs/testPkg\",\n" +
                "  \"parameters\": [\n" +
                "    {\n" +
                "      \"key\": \"name\",\n" +
                "      \"value\": \"test\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"count\",\n" +
                "      \"value\": 1\n" +
                "    }\n" +
                "  ],\n" +
                "  \"publish\": false,\n" +
                "  \"updated\": 1581316522182,\n" +
                "  \"version\": \"0.0.2\"\n" +
                "}\n";
        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> param1 = new LinkedHashMap<>();
        param1.put("key", "name");
        param1.put("value", "test");
        parameters.add(param1);

        Map<String, Object> param2 = new LinkedHashMap<>();
        param2.put("key", "count");
        param2.put("value", 1);
        parameters.add(param2);

        ExecutableWhiskAction expected = new ExecutableWhiskAction("testAct", "testNs/testPkg", "0.0.2", 1581316522182L, false, createActionAnnotation(false, false, false, "nodejs:10"), new Limits(1, 1, 256, 60000), new CodeExec(false, "nodejs:10", "function main(params) {}", "","", new ArrayList<>()), parameters);

        // when
        ExecutableWhiskAction actual = JsonParserUtils.parseWhiskAction(action).get();

        // then
        assertEquals(actual, expected);

    }

    @Test
    public void parseWhiskPackages() throws IOException {

        // given
        String packages = "[\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"description\",\n" +
                "        \"value\": \"test\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"binding\": false,\n" +
                "    \"name\": \"pkg1\",\n" +
                "    \"namespace\": \"ns\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1586513535028,\n" +
                "    \"version\": \"0.0.1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"description\",\n" +
                "        \"value\": \"test\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"binding\": false,\n" +
                "    \"name\": \"pkg2\",\n" +
                "    \"namespace\": \"ns\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1586513513922,\n" +
                "    \"version\": \"0.0.1\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"annotations\": [\n" +
                "      {\n" +
                "        \"key\": \"description\",\n" +
                "        \"value\": \"test\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"binding\",\n" +
                "        \"value\": {\n" +
                "          \"name\": \"sharedpackage\",\n" +
                "          \"namespace\": \"whisk.system\"\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"binding\": {\n" +
                "      \"name\": \"sharedpackage\",\n" +
                "      \"namespace\": \"whisk.system\"\n" +
                "    },\n" +
                "    \"name\": \"pkg3\",\n" +
                "    \"namespace\": \"ns\",\n" +
                "    \"publish\": false,\n" +
                "    \"updated\": 1557380799999,\n" +
                "    \"version\": \"0.0.1\"\n" +
                "  }\n" +
                "]";

        List<WhiskPackage> expected = new ArrayList<>();
        expected.add(new WhiskPackage("pkg1", "ns", false, 1586513535028L, "0.0.1", createPackageAnnotation(null), false));
        expected.add(new WhiskPackage("pkg2", "ns", false, 1586513513922L, "0.0.1", createPackageAnnotation(null), false));
        Map<String, String> binding = createBinding("sharedpackage", "whisk.system");
        expected.add(new WhiskPackage("pkg3", "ns", false, 1557380799999L, "0.0.1", createPackageAnnotation(binding), binding));

        // when
        List<WhiskPackage> actual = JsonParserUtils.parseWhiskPackages(packages);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseWhiskPackage() throws IOException {
        String pkg = "{\n" +
                "  \"namespace\": \"testns\",\n" +
                "  \"name\": \"test\",\n" +
                "  \"version\": \"0.0.6\",\n" +
                "  \"updated\": 1583828352890,\n" +
                "  \"publish\": true,\n" +
                "  \"annotations\": [\n" +
                "    {\n" +
                "      \"key\": \"description\",\n" +
                "      \"value\": \"test\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"parameters\": [\n" +
                "    {\n" +
                "      \"key\": \"test\",\n" +
                "      \"value\": \"test\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"test1\",\n" +
                "      \"value\": \"test1\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"binding\": {},\n" +
                "  \"feeds\": [],\n" +
                "  \"actions\": [\n" +
                "    {\n" +
                "      \"name\": \"action1\",\n" +
                "      \"version\": \"0.0.2\",\n" +
                "      \"annotations\": [\n" +
                "        {\n" +
                "          \"key\": \"exec\",\n" +
                "          \"value\": \"nodejs:10\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"action2\",\n" +
                "      \"version\": \"0.0.2\",\n" +
                "      \"annotations\": [\n" +
                "        {\n" +
                "          \"key\": \"exec\",\n" +
                "          \"value\": \"nodejs:10\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"action3\",\n" +
                "      \"version\": \"0.0.2\",\n" +
                "      \"annotations\": [\n" +
                "        {\n" +
                "          \"key\": \"exec\",\n" +
                "          \"value\": \"nodejs:10\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> param1 = new LinkedHashMap<>();
        param1.put("key", "test");
        param1.put("value", "test");
        parameters.add(param1);

        Map<String, Object> param2 = new LinkedHashMap<>();
        param2.put("key", "test1");
        param2.put("value", "test1");
        parameters.add(param2);

        List<CompactWhiskAction> actions = new ArrayList<>();
        actions.add(new CompactWhiskAction("action1", "0.0.2", createActionAnnotation(false, false, false, "nodejs:10")));
        actions.add(new CompactWhiskAction("action2", "0.0.2", createActionAnnotation(false, false, false, "nodejs:10")));
        actions.add(new CompactWhiskAction("action3", "0.0.2", createActionAnnotation(false, false, false, "nodejs:10")));
        WhiskPackageWithActions expected = new WhiskPackageWithActions("test", "testns", true, 1583828352890L, "0.0.6", createPackageAnnotation(null), new LinkedHashMap<>(), parameters, actions, new ArrayList<>());

        // when
        WhiskPackageWithActions actual = JsonParserUtils.parseWhiskPackage(pkg).get();

        // then
        assertEquals(actual, expected);
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
