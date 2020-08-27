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
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.navercorp.openwhisk.intellij.utils.AnnotationHelper.createRule;
import static com.navercorp.openwhisk.intellij.utils.AnnotationHelper.createTriggerAlarmFeedAnnotation;
import static com.navercorp.openwhisk.intellij.utils.FileHelper.readFile;
import static junit.framework.Assert.assertEquals;

public class WhiskTriggerJsonTest {

    @Test
    public void parseWhiskTriggers() throws IOException {
        // given
        String triggers = readFile("triggers.json");

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
        // given
        String trigger = readFile("trigger.json");

        ExecutableWhiskTrigger expected = new ExecutableWhiskTrigger(
                "trigger1",
                "test",
                "0.0.2",
                1586315245020L,
                false,
                createTriggerAlarmFeedAnnotation(),
                new ArrayList<>(),
                createRule("rule1", "test", "action1"),
                new LinkedHashMap<>());

        // when
        ExecutableWhiskTrigger actual = JsonParserUtils.parseWhiskTrigger(trigger).get();

        // then
        assertEquals(actual, expected);
    }
}
