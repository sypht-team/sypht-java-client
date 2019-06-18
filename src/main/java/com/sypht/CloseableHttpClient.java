/*
 * CloseableHttpClient
 */
package com.sypht;

import org.apache.http.HttpHost;
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
        HttpHost proxy = new HttpHost("proxy.bpay.com.au", 9400);

        this.httpClient = HttpClients
                .custom().setProxy(proxy)
                .setConnectionManager(poolingConnManager)
                .build();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        httpClient.close();
    }
}
