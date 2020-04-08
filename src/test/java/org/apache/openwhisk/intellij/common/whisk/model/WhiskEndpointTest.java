package org.apache.openwhisk.intellij.common.whisk.model;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class WhiskEndpointTest {

    @Test
    public void getApihostWithConstructor() {
        String expected1 = "https://foo.com";
        String expected2 = "http://foo.com";

        WhiskEndpoint auth1 = new WhiskEndpoint("test-auth","https://foo.com", new ArrayList<>());
        WhiskEndpoint auth2 = new WhiskEndpoint("test-auth","foo.com", new ArrayList<>());
        WhiskEndpoint auth3 = new WhiskEndpoint("test-auth","http://foo.com", new ArrayList<>());


        assertEquals(expected1, auth1.getApihost());
        assertEquals(expected1, auth2.getApihost());
        assertEquals(expected2, auth3.getApihost());
    }

    @Test
    public void getApihostWithSetMethod() {
        String expected1 = "http://foo.com";
        String expected2 = "https://bar.com";
        String expected3 = "ftp://foo.com";

        WhiskEndpoint auth1 = new WhiskEndpoint("test-auth","test.com", new ArrayList<>());
        WhiskEndpoint auth2 = new WhiskEndpoint("test-auth","test.com", new ArrayList<>());
        WhiskEndpoint auth3 = new WhiskEndpoint("test-auth","test.com", new ArrayList<>());

        auth1.setApihost("http://foo.com");
        auth2.setApihost("bar.com");
        auth3. setApihost("ftp://foo.com"); // the regular expression does not check the protocol itself but format.

        assertEquals(expected1, auth1.getApihost());
        assertEquals(expected2, auth2.getApihost());
        assertEquals(expected3, auth3.getApihost());
    }
}
