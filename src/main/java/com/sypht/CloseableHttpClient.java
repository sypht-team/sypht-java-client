/*
 * CloseableHttpClient
 */
package com.sypht;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * @author Simon Mittag
 */
public abstract class CloseableHttpClient {
    protected org.apache.http.impl.client.CloseableHttpClient httpClient;

    public CloseableHttpClient() {
        this.httpClient = HttpClients.createDefault();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        httpClient.close();
    }
}
