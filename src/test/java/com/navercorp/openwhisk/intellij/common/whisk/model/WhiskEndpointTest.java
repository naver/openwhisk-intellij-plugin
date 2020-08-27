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

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class WhiskEndpointTest {

    @Test
    public void testWhiskEndpointWithConstructorGetApihost() {
        String expected1 = "https://foo.com";
        String expected2 = "http://foo.com";

        WhiskEndpoint auth1 = new WhiskEndpoint("test-auth", "https://foo.com", new ArrayList<>());
        WhiskEndpoint auth2 = new WhiskEndpoint("test-auth", "foo.com", new ArrayList<>());
        WhiskEndpoint auth3 = new WhiskEndpoint("test-auth", "http://foo.com", new ArrayList<>());


        assertEquals(expected1, auth1.getApihost());
        assertEquals(expected1, auth2.getApihost());
        assertEquals(expected2, auth3.getApihost());
    }

    @Test
    public void testWhiskEndpointWithSetMethodGetApihost() {
        String expected1 = "http://foo.com";
        String expected2 = "https://bar.com";
        String expected3 = "ftp://foo.com";

        WhiskEndpoint auth1 = new WhiskEndpoint("test-auth", "test.com", new ArrayList<>());
        WhiskEndpoint auth2 = new WhiskEndpoint("test-auth", "test.com", new ArrayList<>());
        WhiskEndpoint auth3 = new WhiskEndpoint("test-auth", "test.com", new ArrayList<>());

        auth1.setApihost("http://foo.com");
        auth2.setApihost("bar.com");
        auth3.setApihost("ftp://foo.com"); // the regular expression does not check the protocol itself but format.

        assertEquals(expected1, auth1.getApihost());
        assertEquals(expected2, auth2.getApihost());
        assertEquals(expected3, auth3.getApihost());
    }
}
