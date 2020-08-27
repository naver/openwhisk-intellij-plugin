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

import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class WhiskAuthTest {

    @Test
    public void testGetAuth() {
        String apiKey = "test-auth";
        WhiskAuth auth = new WhiskAuth(apiKey, "test-host");

        // It is seemingly meaningless but it would help to guarantee sanity when the underlying logic is changed.
        assertEquals("Basic " + Base64.getEncoder().encodeToString(apiKey.getBytes()), auth.getBasicAuthHeader());
    }

    @Test
    public void testGetApihost() {
        String expected1 = "https://test-host";
        String expected2 = "http://test-host";
        WhiskAuth auth1 = new WhiskAuth("test-auth", "test-host");
        WhiskAuth auth2 = new WhiskAuth("test-auth", "http://test-host");

        // It is seemingly meaningless but it would help to guarantee sanity when the underlying logic is changed.
        assertEquals(expected1, auth1.getApihost());
        assertEquals(expected2, auth2.getApihost());
    }
}
