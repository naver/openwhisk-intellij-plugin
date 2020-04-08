package org.apache.openwhisk.intellij.common.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class WhiskUtilsTest {
    @Test
    public void getApihHostWithProtocol() {
        String expected1 = "https://foo.com";
        String expected2 = "http://foo.com";

        assertEquals(expected1, WhiskUtils.getApihHostWithProtocol("foo.com"));
        assertEquals(expected2, WhiskUtils.getApihHostWithProtocol("http://foo.com"));
    }
}
