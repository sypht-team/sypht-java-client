package com.sypht;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class SyphtClientTest extends TestCase {

    public SyphtClientTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SyphtClientTest.class);
    }

    /**
     * Test the OAuth Login
     */
    public void testPredictionWithPDF() throws IOException {
        SyphtClient client = new SyphtClient();
        String uuid = client.upload(getTestFile());
        JSONObject prediction = client.result(uuid);

        assert(prediction!=null);
        System.out.println(prediction);

    }

    private File getTestFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("receipt.pdf").getFile());
    }
}
