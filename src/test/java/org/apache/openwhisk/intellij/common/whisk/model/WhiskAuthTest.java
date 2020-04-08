package org.apache.openwhisk.intellij.common.whisk.model;

import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

public class WhiskAuthTest {

    @Test
    public void getAuthTest() {
        String apiKey = "test-auth";
        WhiskAuth auth = new WhiskAuth(apiKey, "test-host");

        // It is seemingly meaningless but it would help to guarantee sanity when the underlying logic is changed.
        assertEquals("Basic " + Base64.getEncoder().encodeToString(apiKey.getBytes()), auth.getBasicAuthHeader());
    }

    @Test
    public void getApihostTest() {
        String expected1 = "https://test-host";
        String expected2 = "http://test-host";
        WhiskAuth auth1 = new WhiskAuth("test-auth", "test-host");
        WhiskAuth auth2 = new WhiskAuth("test-auth", "http://test-host");

        // It is seemingly meaningless but it would help to guarantee sanity when the underlying logic is changed.
        assertEquals(expected1, auth1.getApihost());
        assertEquals(expected2, auth2.getApihost());
    }
}
