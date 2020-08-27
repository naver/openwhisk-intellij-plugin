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

import com.navercorp.openwhisk.intellij.common.Icons;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.CodeExec;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.ExecMetaData;
import com.navercorp.openwhisk.intellij.explorer.editor.model.ComboBoxEntityEntry;
import com.navercorp.openwhisk.intellij.explorer.editor.model.ComboBoxEntityType;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.navercorp.openwhisk.intellij.utils.AnnotationHelper.createActionAnnotation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WhiskActionTest {
    private String name = "testName";
    private String namespace = "testNamespace";
    private String pkg = "testPkg";
    private String version = "1.0.0";
    private long updated = 1581316522182L;
    private boolean publish = false;
    private List<Map<String, Object>> annotations = createActionAnnotation(false, false, false, false, "nodejs:12");
    private Limits limits = new Limits(1, 1, 256, 60000);
    private ExecMetaData exec = new ExecMetaData(false);

    @Test
    public void testGetWhiskPackage() {
        String expectedPkg = pkg;

        WhiskActionMetaData action = new WhiskActionMetaData(name, namespace, version, updated, publish, annotations, limits, exec);
        WhiskActionMetaData actionWithPkg = new WhiskActionMetaData(name, namespace + "/" + pkg, version, updated, publish, annotations, limits, exec);

        assertEquals(Optional.empty(), action.getWhiskPackage());
        assertEquals(Optional.of(expectedPkg), actionWithPkg.getWhiskPackage());
    }


    @Test
    public void testGetNamespacePath() {
        String expectedNamespace = namespace;

        WhiskActionMetaData action = new WhiskActionMetaData(name, namespace, version, updated, publish, annotations, limits, exec);
        WhiskActionMetaData actionWithPkg = new WhiskActionMetaData(name, namespace + "/" + pkg, version, updated, publish, annotations, limits, exec);

        assertEquals(expectedNamespace, action.getNamespacePath());
        assertEquals(expectedNamespace, actionWithPkg.getNamespacePath());
    }

    @Test
    public void testGetFullyQualifiedName() {
        String expectedFQN = namespace + "/" + name;
        String expectedPkgFQN = namespace + "/" + pkg + "/" + name;

        WhiskActionMetaData action = new WhiskActionMetaData(name, namespace, version, updated, publish, annotations, limits, exec);
        WhiskActionMetaData actionWithPkg = new WhiskActionMetaData(name, namespace + "/" + pkg, version, updated, publish, annotations, limits, exec);

        assertEquals(expectedFQN, action.getFullyQualifiedName());
        assertEquals(expectedPkgFQN, actionWithPkg.getFullyQualifiedName());
    }


    @Test
    public void testGetKind() {
        String expectedNodeKind = "nodejs:12";
        String expectedRubyKind = "ruby:2.5";
        String expectedPhpKind = "php:7.3";

        List<Map<String, Object>> nodeKindAnnotations = createActionAnnotation(false, false, false, false, expectedNodeKind);
        List<Map<String, Object>> rubyKindAnnotations = createActionAnnotation(false, false, false, false, expectedRubyKind);
        List<Map<String, Object>> phpKindAnnotations = createActionAnnotation(false, false, false, false, expectedPhpKind);

        WhiskActionMetaData nodeAction = new WhiskActionMetaData(name, namespace, version, updated, publish, nodeKindAnnotations, limits, exec);
        WhiskActionMetaData rubyAction = new WhiskActionMetaData(name, namespace, version, updated, publish, rubyKindAnnotations, limits, exec);
        WhiskActionMetaData phpAction = new WhiskActionMetaData(name, namespace, version, updated, publish, phpKindAnnotations, limits, exec);

        assertEquals(expectedNodeKind, nodeAction.getKind());
        assertEquals(expectedRubyKind, rubyAction.getKind());
        assertEquals(expectedPhpKind, phpAction.getKind());
    }

    @Test
    public void testGetKindExtension() {
        String expectedNodeExtension = ".js";
        String expectedRubyExtension = ".rb";
        String expectedPhpExtension = ".php";

        String nodeKind = "nodejs:12";
        String rubyKind = "ruby:2.5";
        String phpKind = "php:7.3";

        List<Map<String, Object>> nodeKindAnnotations = createActionAnnotation(false, false, false, false, nodeKind);
        List<Map<String, Object>> rubyKindAnnotations = createActionAnnotation(false, false, false, false, rubyKind);
        List<Map<String, Object>> phpKindAnnotations = createActionAnnotation(false, false, false, false, phpKind);

        WhiskActionMetaData nodeAction = new WhiskActionMetaData(name, namespace, version, updated, publish, nodeKindAnnotations, limits, exec);
        WhiskActionMetaData rubyAction = new WhiskActionMetaData(name, namespace, version, updated, publish, rubyKindAnnotations, limits, exec);
        WhiskActionMetaData phpAction = new WhiskActionMetaData(name, namespace, version, updated, publish, phpKindAnnotations, limits, exec);

        assertEquals(expectedNodeExtension, nodeAction.getKindExtension());
        assertEquals(expectedRubyExtension, rubyAction.getKindExtension());
        assertEquals(expectedPhpExtension, phpAction.getKindExtension());
    }


    @Test
    public void testGetKindIcon() {
        Icon expectedNodeIcon = Icons.KIND_JS;
        Icon expectedRubyIcon = Icons.KIND_RUBY;
        Icon expectedPhpIcon = Icons.KIND_PHP;

        String nodeKind = "nodejs:12";
        String rubyKind = "ruby:2.5";
        String phpKind = "php:7.3";

        List<Map<String, Object>> nodeKindAnnotations = createActionAnnotation(false, false, false, false, nodeKind);
        List<Map<String, Object>> rubyKindAnnotations = createActionAnnotation(false, false, false, false, rubyKind);
        List<Map<String, Object>> phpKindAnnotations = createActionAnnotation(false, false, false, false, phpKind);

        WhiskActionMetaData nodeAction = new WhiskActionMetaData(name, namespace, version, updated, publish, nodeKindAnnotations, limits, exec);
        WhiskActionMetaData rubyAction = new WhiskActionMetaData(name, namespace, version, updated, publish, rubyKindAnnotations, limits, exec);
        WhiskActionMetaData phpAction = new WhiskActionMetaData(name, namespace, version, updated, publish, phpKindAnnotations, limits, exec);

        assertEquals(expectedNodeIcon, nodeAction.getKindIcon());
        assertEquals(expectedRubyIcon, rubyAction.getKindIcon());
        assertEquals(expectedPhpIcon, phpAction.getKindIcon());
    }


    @Test
    public void testIsSequenceAction() {
        List<Map<String, Object>> sequenceAnnotations = createActionAnnotation(false, false, false, false, "sequence");

        List<Map<String, Object>> parameters = new ArrayList<>();

        List<String> components = new ArrayList<>();
        components.add("/testNs/testAction1");
        components.add("/testNs/testAction2");
        components.add("/testNs/testAction3");

        CodeExec codeExec = new CodeExec(false, "sequence", null, null, null, components);

        ExecutableWhiskAction sequenceAction = new ExecutableWhiskAction(name, namespace, version, updated, publish,
                sequenceAnnotations, limits, codeExec, parameters);

        assertTrue(sequenceAction.isSequenceAction());
    }

    @Test
    public void testWhiskActionWebActionOptions() {
        List<Map<String, Object>> webAnnotations = createActionAnnotation(true, false, false, false, "nodejs:12");
        List<Map<String, Object>> rawHttpAnnotations = createActionAnnotation(true, true, false, false, "nodejs:12");
        List<Map<String, Object>> customOptionsAnnotations = createActionAnnotation(true, true, true, false, "nodejs:12");
        List<Map<String, Object>> finalAnnotations = createActionAnnotation(true, false, false, true, "nodejs:12");

        WhiskActionMetaData webAction = new WhiskActionMetaData(name, namespace, version, updated, publish, webAnnotations, limits, exec);
        WhiskActionMetaData rawHttpAction = new WhiskActionMetaData(name, namespace, version, updated, publish, rawHttpAnnotations, limits, exec);
        WhiskActionMetaData customOptionsAction = new WhiskActionMetaData(name, namespace, version, updated, publish, customOptionsAnnotations, limits, exec);
        WhiskActionMetaData finalAction = new WhiskActionMetaData(name, namespace, version, updated, publish, finalAnnotations, limits, exec);

        assertTrue(webAction.isWebAction());

        assertTrue(rawHttpAction.isWebAction());
        assertTrue(rawHttpAction.isRawHttp());

        assertTrue(customOptionsAction.isWebAction());
        assertTrue(customOptionsAction.isRawHttp());

        assertTrue(finalAction.isWebAction());
        assertTrue(finalAction.isFinalDefaultParameter());
    }

    @Test
    public void testWhiskActionMetaDataToString() {
        String expected = name;

        WhiskActionMetaData action = new WhiskActionMetaData(name, namespace, version, updated, publish, annotations, limits, exec);

        assertEquals(expected, action.toString());
    }

    @Test
    public void testWhiskActionMetaDataWithPackageToString() {
        String expected = pkg + "/" + name;

        WhiskActionMetaData actionWithPackage = new WhiskActionMetaData(name, namespace + "/" + pkg, version, updated, publish, annotations, limits, exec);

        assertEquals(expected, actionWithPackage.toString());
    }

    @Test
    public void testWhiskActionMetaDataToCombBoxEntityEntry() {
        WhiskActionMetaData actionMetaData = new WhiskActionMetaData(name, namespace, version, updated, publish, annotations, limits, exec);

        ComboBoxEntityEntry expectedEntry = new ComboBoxEntityEntry(actionMetaData.toString(), ComboBoxEntityType.ACTION);

        assertEquals(expectedEntry, actionMetaData.toCombBoxEntityEntry());
    }
}
