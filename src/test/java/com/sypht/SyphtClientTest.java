package com.sypht;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileInputStream;
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
     * Test the bearer token can be cached. No special assertions here, just passing predictions
     */
    public void testPredictionWithCachedToken() throws IOException {
        SyphtClient client = new SyphtClient();
        Map<String, String> options = new HashMap<>();
        options.put("fieldSets", "[\"sypht.invoice\"]");

        String uuid = client.upload(getTestFile(), options);
        String prediction = client.result(uuid);

        //2nd attempt should use cached token
        prediction = client.result(uuid);
        System.out.println(prediction);

        //this will force expiry of the cached token
        client.OAUTH_GRACE_PERIOD = 1000 * 60 * 60 * 24;

        //3rd attempt should force token refresh.
        prediction = client.result(uuid);
        System.out.println(prediction);

        assert (prediction.contains("results"));
        System.out.println(prediction);
    }

    /**
     * Test prediction, this time with custom fieldset
     */
    public void testPredictionWithPDFAndCustomFieldset() throws IOException {
        SyphtClient client = new SyphtClient();
        Map<String, String> options = new HashMap<>();
        options.put("fieldSets", "[\"sypht.invoice\"]");
        options.put("fileToUpload", "file.pdf");
        String uuid = client.upload(getTestFile(), options);
        String prediction = client.result(uuid);

        assert (prediction.contains("invoice.total"));
        System.out.println(prediction);
    }

    /**
     * Test prediction, this time with custom fieldset
     */
    public void testPredictionWithPDFStreamAndCustomFieldset() throws IOException {
        SyphtClient client = new SyphtClient();
        Map<String, String> options = new HashMap<>();
        options.put("fieldSets", "[\"sypht.invoice\"]");
        options.put("fileToUpload", "file.pdf");
        String uuid = client.upload("file.pdf", new FileInputStream(getTestFile()), options);
        String prediction = client.result(uuid);

        assert (prediction.contains("invoice.total"));
        System.out.println(prediction);
    }

    /**
     * Test prediction with http proxy. Validate free proxy server frequently
     */
    public void testPredictionWithSocksProxy() throws IOException {
        System.setProperty("socksHost", "50.62.59.61");
        System.setProperty("socksPort", "1431");

        SyphtClient client = new SyphtClient();
        Map<String, String> options = new HashMap<>();
        options.put("fieldSets", "[\"sypht.invoice\"]");
        options.put("fileToUpload", "file.pdf");
        String uuid = client.upload("file.pdf", new FileInputStream(getTestFile()), options);
        String prediction = client.result(uuid);

        assert (prediction.contains("invoice.total"));
        System.out.println(prediction);
    }

    protected File getTestFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        return new File(classLoader.getResource("receipt.pdf").getFile());
    }
}
