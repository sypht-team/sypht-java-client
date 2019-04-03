package com.sypht;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class UploadTest extends TestCase {

    public UploadTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(UploadTest.class);
    }

    /**
     * Test the OAuth Login
     */
    public void testUpload() throws IOException {
        SyphtClient client = new SyphtClient(new OAuthClient().login());
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("receipt.pdf").getFile());
        String response = client.upload(file);
        System.out.println(response);

    }
}
