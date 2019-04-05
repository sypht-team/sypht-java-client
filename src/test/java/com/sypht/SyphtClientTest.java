package com.sypht;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for Sypht HTTP Client.
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
     * Make a simple prediction
     */
    public void testPredictionWithCachedToken() throws IOException {
        SyphtClient client = new SyphtClient();
        String uuid = client.upload(getTestFile());
        String prediction = client.result(uuid);

        //2nd attempt should use cached token
        prediction = client.result(uuid);

        assert(prediction.contains("results"));
        System.out.println(prediction);
    }

    /**
     * Prediction, this time with custom fieldset
     */
    public void testPredictionWithPDFAndCustomFieldset() throws IOException {
        SyphtClient client = new SyphtClient();
        Map<String, String> options = new HashMap<>();
        options.put("fieldSet", "invoice");
        String uuid = client.upload(getTestFile(), options);
        String prediction = client.result(uuid);

        assert(prediction.contains("invoice.total"));
        System.out.println(prediction);
    }


    protected File getTestFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("receipt.pdf").getFile());
    }
}
