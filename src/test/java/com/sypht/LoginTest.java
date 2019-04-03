package com.sypht;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class LoginTest extends TestCase {


    public LoginTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(LoginTest.class);
    }

    /**
     * Test the OAuth Login
     */
    public void testLogin() throws IOException {

        OAuthClient client = new OAuthClient();
        String token = client.login();
        assertTrue("no response from login service", token!=null);
        assertTrue("doesn't look like a JWT Token", token.startsWith("eyJ0"));
    }
}
