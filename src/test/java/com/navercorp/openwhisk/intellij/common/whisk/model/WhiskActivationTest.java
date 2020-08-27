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

import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationWithLogs;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class WhiskActivationTest {
    private String expectedActivationId = "969c2bf39e3b40599c2bf59e3b901911";
    private String expectedName = "testName";
    private String expectedNamespace = "testNamespace";
    private String expectedVersion = "1.0.0";
    private String expectedCause = "51adf3c3f875df3gadf3c0f875ef3eb9";
    private long expectedStart = 1592555196109L;
    private long expectedEnd = 1592555196153L;
    private long expectedDuration = 50;
    private boolean expectedPublish = false;

    @Test
    public void testWhiskActionMetaDataGetter() {

        List<Map<String, Object>> expectedAnnotations = new ArrayList<>();
        int expectedStatusCode = 0;

        WhiskActivationMetaData activationMetaData = new WhiskActivationMetaData(
                expectedActivationId, expectedName, expectedNamespace, expectedVersion, expectedCause,
                expectedStart, expectedEnd, expectedDuration, expectedPublish, expectedAnnotations, expectedStatusCode);

        assertEquals(expectedActivationId, activationMetaData.getActivationId());
        assertEquals(expectedName, activationMetaData.getName());
        assertEquals(expectedNamespace, activationMetaData.getNamespace());
        assertEquals(expectedVersion, activationMetaData.getVersion());
        assertEquals(expectedCause, activationMetaData.getCause());
        assertEquals(expectedStart, activationMetaData.getStart());
        assertEquals(expectedEnd, activationMetaData.getEnd());
        assertEquals(expectedDuration, activationMetaData.getDuration());
        assertEquals(expectedPublish, activationMetaData.isPublish());
        assertEquals(expectedAnnotations, activationMetaData.getAnnotations());
        assertEquals(expectedStatusCode, activationMetaData.getStatusCode());
    }

    @Test
    public void testWhiskActionMetaDataGetStatus() {
        List<Map<String, Object>> expectedAnnotations = new ArrayList<>();

        int success = 0;
        String expectedSuccessMessage0 = "success";
        WhiskActivationMetaData activationMetaData0 = new WhiskActivationMetaData(
                expectedActivationId, expectedName, expectedNamespace, expectedVersion, expectedCause,
                expectedStart, expectedEnd, expectedDuration, expectedPublish, expectedAnnotations, success);
        assertEquals(expectedSuccessMessage0, activationMetaData0.getStatus());


        int applicationError = 1;
        String expectedSuccessMessage1 = "application error";
        WhiskActivationMetaData activationMetaData1 = new WhiskActivationMetaData(
                expectedActivationId, expectedName, expectedNamespace, expectedVersion, expectedCause,
                expectedStart, expectedEnd, expectedDuration, expectedPublish, expectedAnnotations, applicationError);
        assertEquals(expectedSuccessMessage1, activationMetaData1.getStatus());

        int developerError = 2;
        String expectedSuccessMessage2 = "developer error";
        WhiskActivationMetaData activationMetaData2 = new WhiskActivationMetaData(
                expectedActivationId, expectedName, expectedNamespace, expectedVersion, expectedCause,
                        expectedStart, expectedEnd, expectedDuration, expectedPublish, expectedAnnotations, developerError);
        assertEquals(expectedSuccessMessage2, activationMetaData2.getStatus());


        int internalError = 3;
        String expectedSuccessMessage3 = "internal error";
        WhiskActivationMetaData activationMetaData3 = new WhiskActivationMetaData(
                expectedActivationId, expectedName, expectedNamespace, expectedVersion, expectedCause,
                expectedStart, expectedEnd, expectedDuration, expectedPublish, expectedAnnotations, internalError);
        assertEquals(expectedSuccessMessage3, activationMetaData3.getStatus());
    }

    @Test
    public void testWhiskActivationWithLogsGetter() {
        List<Map<String, Object>> expectedAnnotations = new ArrayList<>();
        String expectedSubject = "testNamespace";
        List<String> expectedLogs = new ArrayList<>();
        Map<String, Object> expectedResponse = new LinkedHashMap<>();

        WhiskActivationWithLogs activationWithLogs = new WhiskActivationWithLogs(
                expectedActivationId, expectedName, expectedNamespace, expectedVersion, expectedCause,
                expectedStart, expectedEnd, expectedDuration, expectedPublish, expectedAnnotations, expectedSubject, expectedLogs, expectedResponse);

        assertEquals(expectedActivationId, activationWithLogs.getActivationId());
        assertEquals(expectedName, activationWithLogs.getName());
        assertEquals(expectedNamespace, activationWithLogs.getNamespace());
        assertEquals(expectedVersion, activationWithLogs.getVersion());
        assertEquals(expectedCause, activationWithLogs.getCause());
        assertEquals(expectedStart, activationWithLogs.getStart());
        assertEquals(expectedEnd, activationWithLogs.getEnd());
        assertEquals(expectedDuration, activationWithLogs.getDuration());
        assertEquals(expectedPublish, activationWithLogs.isPublish());
        assertEquals(expectedAnnotations, activationWithLogs.getAnnotations());
        assertEquals(expectedSubject, activationWithLogs.getSubject());
        assertEquals(expectedLogs, activationWithLogs.getLogs());
        assertEquals(expectedResponse, activationWithLogs.getResponse());
    }
}
