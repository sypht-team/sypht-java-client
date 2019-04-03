package com.sypht;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class OAuthClientTest extends TestCase {
    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    public OAuthClientTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        environmentVariables.set("OAUTH_AUDIENCE", "https://api.sypht.com");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(OAuthClientTest.class);
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
