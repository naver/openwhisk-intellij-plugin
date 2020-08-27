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

import com.navercorp.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.navercorp.openwhisk.intellij.utils.AnnotationHelper.*;
import static org.junit.Assert.*;

public class WhiskPackageTest {
    private String name = "testPkg";
    private String namespace = "testNamespace";
    private String version = "1.0.0";
    private long updated = 1581316522182L;
    private boolean publish = false;

    @Test
    public void testWhiskPackageGetBinding() {
        List<Map<String, Object>> annotations = createPackageAnnotation("test-description", null);
        WhiskPackage notBoundPackage = new WhiskPackage(name, namespace, publish, updated, version, annotations, createFalseBinding());

        assertFalse(notBoundPackage.getBinding().isPresent());
    }

    @Test
    public void testBoundWhiskPackageGetBinding() {
        Binding binding = new Binding("boundPkg", "boundNamespace");

        Map<String, String> bindingMap = createBinding(binding.getName(), binding.getNamespace());
        List<Map<String, Object>> annotations = createPackageAnnotation("test-description", bindingMap);
        WhiskPackage boundPackage = new WhiskPackage(name, namespace, publish, updated, version, annotations, bindingMap);

        assertTrue(boundPackage.getBinding().isPresent());
        assertEquals(binding, Optional.of(binding).get());
    }


    @Test
    public void testWhiskPackageWithActionsGetBinding() {
        List<Map<String, Object>> parameters = new ArrayList<>();
        List<CompactWhiskAction> actions = new ArrayList<>();
        List<Object> feeds = new ArrayList<>();

        List<Map<String, Object>> annotations = createPackageAnnotation("test-description", null);
        WhiskPackageWithActions notBoundPackage = new WhiskPackageWithActions(name, namespace, publish, updated, version, annotations,
                createEmptyBinding(), parameters, actions, feeds);

        assertFalse(notBoundPackage.getBinding().isPresent());
    }

    @Test
    public void testBoundWhiskPackageWithActionsGetBinding() {
        List<Map<String, Object>> parameters = new ArrayList<>();
        List<CompactWhiskAction> actions = new ArrayList<>();
        List<Object> feeds = new ArrayList<>();

        Binding binding = new Binding("boundPkg", "boundNamespace");

        Map<String, String> bindingMap = createBinding(binding.getName(), binding.getNamespace());
        List<Map<String, Object>> annotations = createPackageAnnotation("test-description", bindingMap);
        WhiskPackageWithActions boundPackage = new WhiskPackageWithActions(name, namespace, publish, updated, version,
                annotations, bindingMap, parameters, actions, feeds);

        assertTrue(boundPackage.getBinding().isPresent());
        assertEquals(binding, Optional.of(binding).get());
    }

}
