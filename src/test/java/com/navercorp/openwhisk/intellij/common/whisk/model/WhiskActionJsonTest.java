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

package com.navercorp.openwhisk.intellij.common.whisk.model;

import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.CodeExec;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.ExecMetaData;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.navercorp.openwhisk.intellij.utils.AnnotationHelper.createActionAnnotation;
import static com.navercorp.openwhisk.intellij.utils.FileHelper.readFile;

public class WhiskActionJsonTest {

    @Test
    public void testParseWhiskActionList() throws IOException {
        // given
        String actions = readFile("actions.json");

        List<WhiskAction> expected = new ArrayList<>();
        expected.add(new WhiskActionMetaData("testAct1", "testNs/testPkg1", "0.0.1", 1585756253499L, false,
                createActionAnnotation(true, false, false, true, "nodejs:12"),
                new Limits(1, 1, 256, 60000),
                new ExecMetaData(true)));
        expected.add(new WhiskActionMetaData("testAct2", "testNs/testPkg2", "0.0.1", 1585756252393L, false,
                createActionAnnotation(true, false, false, true, "nodejs:12"),
                new Limits(1, 1, 256, 60000),
                new ExecMetaData(true)));
        expected.add(new WhiskActionMetaData("testAct3", "testNs/testPkg3", "0.0.1", 1585756251289L, false,
                createActionAnnotation(true, false, false, true, "nodejs:12"),
                new Limits(1, 1, 256, 60000), new ExecMetaData(true)));
        expected.add(new WhiskActionMetaData("testAct4", "testNs/testPkg4", "0.0.2", 1582709807073L, false,
                createActionAnnotation(false, false, false, false, "blackbox"),
                new Limits(1, 1, 256, 60000), new ExecMetaData(false)));
        expected.add(new WhiskActionMetaData("testAct5", "testNs/testPkg5", "0.0.1", 1581316522182L, false,
                createActionAnnotation(false, false, false, false, "python:3"),
                new Limits(1, 1, 256, 60000), new ExecMetaData(false)));

        // when
        List<WhiskActionMetaData> actual = JsonParserUtils.parseWhiskActions(actions);

        // then
        Assert.assertEquals(expected, actual);
    }


    @Test
    public void testParseNormalWhiskAction() throws IOException {
        // given
        String action = readFile("action.json");

        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> param1 = new LinkedHashMap<>();
        param1.put("key", "name");
        param1.put("value", "test");
        parameters.add(param1);

        Map<String, Object> param2 = new LinkedHashMap<>();
        param2.put("key", "count");
        param2.put("value", 1);
        parameters.add(param2);

        ExecutableWhiskAction expected = new ExecutableWhiskAction(
                "testAct",
                "testNs/testPkg",
                "0.0.1",
                1581316522182L,
                false,
                createActionAnnotation(false, false, false, false, "nodejs:12"),
                new Limits(1, 1, 256, 60000),
                new CodeExec(false, "nodejs:12", null, "function main(params) {}", null, new ArrayList<>()),
                parameters);

        // when
        ExecutableWhiskAction actual = JsonParserUtils.parseWhiskAction(action).get();

        // then
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNormalBinaryAction() throws IOException {
        // given
        String action = readFile("binary-action.json");

        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> param1 = new LinkedHashMap<>();
        param1.put("key", "name");
        param1.put("value", "test");
        parameters.add(param1);

        Map<String, Object> param2 = new LinkedHashMap<>();
        param2.put("key", "count");
        param2.put("value", 1);
        parameters.add(param2);

        ExecutableWhiskAction expected = new ExecutableWhiskAction(
                "testAct",
                "testNs/testPkg",
                "0.0.1",
                1585756253499L,
                false,
                createActionAnnotation(false, false, false, false, "nodejs:12"),
                new Limits(1, 1, 256, 60000),
                new CodeExec(true, "nodejs:12", null, "aaaaa", null, new ArrayList<>()),
                parameters);

        // when
        ExecutableWhiskAction actual = JsonParserUtils.parseWhiskAction(action).get();

        // then
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseJavaAction() throws IOException {
        // given
        String action = readFile("java-action.json");

        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> param1 = new LinkedHashMap<>();
        param1.put("key", "name");
        param1.put("value", "test");
        parameters.add(param1);

        Map<String, Object> param2 = new LinkedHashMap<>();
        param2.put("key", "count");
        param2.put("value", 1);
        parameters.add(param2);

        ExecutableWhiskAction expected = new ExecutableWhiskAction(
                "testAct",
                "testNs/testPkg",
                "0.0.1",
                1585756253499L,
                false,
                createActionAnnotation(false, false, false, false, "java"),
                new Limits(1, 1, 256, 60000),
                new CodeExec(true, "java", "test", "aaaaa", null, new ArrayList<>()),
                parameters);

        // when
        ExecutableWhiskAction actual = JsonParserUtils.parseWhiskAction(action).get();

        // then
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testParseSequenceAction() throws IOException {
        // given
        String action = readFile("sequence-action.json");

        List<String> components = new ArrayList<>();
        components.add("/testNs/testAct1");
        components.add("/testNs/testAct2");

        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> param1 = new LinkedHashMap<>();
        param1.put("key", "_actions");
        param1.put("components", components);
        parameters.add(param1);

        ExecutableWhiskAction expected = new ExecutableWhiskAction(
                "testAct",
                "testNs/testPkg",
                "0.0.1",
                1585756253499L,
                false,
                createActionAnnotation(false, false, false, false, "sequence"),
                new Limits(1, 1, 256, 60000),
                new CodeExec(false, "sequence", null, null, null, components),
                parameters);

        // when
        ExecutableWhiskAction actual = JsonParserUtils.parseWhiskAction(action).get();

        // then
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testParseBlackboxAction() throws IOException {
        // given
        String action = readFile("blackbox-action.json");

        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> param1 = new LinkedHashMap<>();
        param1.put("key", "name");
        param1.put("value", "test");
        parameters.add(param1);

        Map<String, Object> param2 = new LinkedHashMap<>();
        param2.put("key", "count");
        param2.put("value", 1);
        parameters.add(param2);

        ExecutableWhiskAction expected = new ExecutableWhiskAction(
                "testAct",
                "testNs/testPkg",
                "0.0.1",
                1585756253499L,
                false,
                createActionAnnotation(false, false, false, false, "blackbox"),
                new Limits(1, 1, 256, 60000),
                new CodeExec(true, "blackbox", null, null, "imageRepo/imageName", new ArrayList<>()),
                parameters);

        // when
        ExecutableWhiskAction actual = JsonParserUtils.parseWhiskAction(action).get();

        // then
        Assert.assertEquals(expected, actual);
    }
}
