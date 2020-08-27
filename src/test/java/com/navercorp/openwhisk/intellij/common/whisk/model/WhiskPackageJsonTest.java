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
import com.navercorp.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.navercorp.openwhisk.intellij.utils.AnnotationHelper.*;
import static com.navercorp.openwhisk.intellij.utils.FileHelper.readFile;
import static junit.framework.Assert.assertEquals;

public class WhiskPackageJsonTest {

    @Test
    public void parseWhiskPackages() throws IOException {
        // given
        String packages = readFile("packages.json");

        List<WhiskPackage> expected = new ArrayList<>();
        expected.add(new WhiskPackage("pkg1", "ns", false, 1586513535028L, "0.0.1", createPackageAnnotation("test", null), false));
        expected.add(new WhiskPackage("pkg2", "ns", false, 1586513513922L, "0.0.1", createPackageAnnotation("test", null), false));
        Map<String, String> binding = createBinding("sharedpackage", "whisk.system");
        expected.add(new WhiskPackage("pkg3", "ns", false, 1557380799999L, "0.0.1", createPackageAnnotation("test", binding), binding));

        // when
        List<WhiskPackage> actual = JsonParserUtils.parseWhiskPackages(packages);

        // then
        assertEquals(expected, actual);
    }


    @Test
    public void parseWhiskPackage() throws IOException {
        // given
        String pkg = readFile("package.json");

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
        actions.add(new CompactWhiskAction("action1", "0.0.2", createActionAnnotation(false, false, false, false, "nodejs:10")));
        actions.add(new CompactWhiskAction("action2", "0.0.2", createActionAnnotation(false, false, false, false, "nodejs:10")));
        actions.add(new CompactWhiskAction("action3", "0.0.2", createActionAnnotation(false, false, false, false, "nodejs:10")));
        WhiskPackageWithActions expected = new WhiskPackageWithActions("test", "testns", true, 1583828352890L, "0.0.6",
                createPackageAnnotation("test", null), createEmptyBinding(), parameters, actions, new ArrayList<>());

        // when
        WhiskPackageWithActions actual = JsonParserUtils.parseWhiskPackage(pkg).get();

        // then
        assertEquals(expected, actual);
    }
}
