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
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationWithLogs;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskRule;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class EmptyJsonParserUtilsTests {

    @Test
    public void parseEmptyWhiskActions() throws IOException {
        // given
        String empty = "";

        List<WhiskAction> expected = new ArrayList<>();

        // when
        List<WhiskActionMetaData> actual = JsonParserUtils.parseWhiskActions(empty);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseEmtpyWhiskAction() throws IOException {
        // given
        String empty = "";

        // when
        Optional<ExecutableWhiskAction> actual = JsonParserUtils.parseWhiskAction(empty);

        // then
        assertFalse(actual.isPresent());
    }

    @Test
    public void parseEmptyWhiskPackages() throws IOException {
        // given
        String empty = "";

        List<WhiskPackage> expected = new ArrayList<>();

        // when
        List<WhiskPackage> actual = JsonParserUtils.parseWhiskPackages(empty);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseEmtpyWhiskPackage() throws IOException {
        // given
        String empty = "";

        // when
        Optional<WhiskPackageWithActions> actual = JsonParserUtils.parseWhiskPackage(empty);

        // then
        assertFalse(actual.isPresent());
    }

    @Test
    public void parseEmptyWhiskEndpoints() throws IOException {
        // given
        String empty = "";

        List<WhiskEndpoint> expected = new ArrayList<>();

        // when
        List<WhiskEndpoint> actual = JsonParserUtils.parseWhiskEndpoints(empty);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseEmptyWhiskNamespaces() throws IOException {
        // given
        String empty = "";

        // when
        String[] actual = JsonParserUtils.parseWhiskNamespace(empty);

        // then
        assertEquals(actual.length, 0);

    }

    @Test
    public void parseEmptyWhiskActivations() throws IOException {
        // given
        String empty = "";

        List<WhiskActivationMetaData> expected = new ArrayList<>();

        // when
        List<WhiskActivationMetaData> actual = JsonParserUtils.parseWhiskActivations(empty);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseEmtpyActivation() throws IOException {
        // given
        String empty = "";

        // when
        Optional<WhiskActivationWithLogs> actual = JsonParserUtils.parseWhiskActivation(empty);

        // then
        assertFalse(actual.isPresent());
    }

    @Test
    public void parseEmptyWhiskTriggers() throws IOException {
        // given
        String empty = "";

        List<WhiskTriggerMetaData> expected = new ArrayList<>();

        // when
        List<WhiskTriggerMetaData> actual = JsonParserUtils.parseWhiskTriggers(empty);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseEmtpyTrigger() throws IOException {
        // given
        String empty = "";

        // when
        Optional<ExecutableWhiskTrigger> actual = JsonParserUtils.parseWhiskTrigger(empty);

        // then
        assertFalse(actual.isPresent());
    }

    @Test
    public void parseEmtpyRule() throws IOException {
        // given
        String empty = "";

        // when
        Optional<WhiskRule> actual = JsonParserUtils.parseWhiskRule(empty);

        // then
        assertFalse(actual.isPresent());
    }

    @Test
    public void parseEmptyListMap() throws IOException {
        // given
        String empty = "";

        List<Map<String, Object>> expected = new ArrayList<>();

        // when
        List<Map<String, Object>> actual = JsonParserUtils.parseListMap(empty);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void parseEmptyMap() throws IOException {
        // given
        String empty = "";

        Map<String, Object> expected = new LinkedHashMap<>();

        // when
        Map<String, Object> actual = JsonParserUtils.parseMap(empty);

        // then
        assertEquals(actual, expected);
    }

    @Test
    public void beautifyEmptyJson() throws IOException {
        // given
        String empty = "";

        // when
        String actual = JsonParserUtils.beautifyJson(empty);

        // then
        assertEquals(actual, "");
    }


}
