/*
 * CloseableHttpClient
 */
package com.sypht;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author Simon Mittag
 */
public abstract class CloseableHttpClient {
    protected org.apache.http.impl.client.CloseableHttpClient httpClient;

    public CloseableHttpClient() {

        HttpClientConnectionManager poolingConnManager
                = new PoolingHttpClientConnectionManager();

        this.httpClient = HttpClients
                .custom()
                .setConnectionManager(poolingConnManager)
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        httpClient.close();
    }
}
