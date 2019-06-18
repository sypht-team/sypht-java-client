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

        HttpHost proxy = getSystemProxy();
        if (proxy!=null) {
            this.httpClient = HttpClients
                    .custom().setProxy(proxy)
                    .setConnectionManager(poolingConnManager)
                    .build();
        } else {
            this.httpClient = HttpClients
                    .custom()
                    .setConnectionManager(poolingConnManager)
                    .build();
        }
    }

    private HttpHost getSystemProxy() {
        HttpHost proxy = null;
        String proxyHost = null;
        Integer proxyPort = null;
        String httpProxyHost = System.getProperty("http.proxyHost");
        String httpsProxyHost = System.getProperty("https.proxyHost");
        String socksProxyHost = System.getProperty("socksProxyHost");

        if (httpProxyHost != null) {
            proxyHost = httpProxyHost;
            String httpProxyPortStr = System.getProperty("http.proxyPort");
            if (httpProxyPortStr == null) {
                proxyPort = 80;
            } else {
                proxyPort = Integer.parseInt(httpProxyPortStr);
            }
        } else if (httpsProxyHost != null) {
            proxyHost = httpsProxyHost;
            String httpsProxyPortStr = System.getProperty("https.proxyPort");
            if (httpsProxyPortStr == null) {
                proxyPort = 443;
            } else {
                proxyPort = Integer.parseInt(httpsProxyPortStr);
            }
        } else if (socksProxyHost != null) {
            proxyHost = socksProxyHost;
            String socksProxyPortStr = System.getProperty("socksProxyPort");
            if (socksProxyPortStr == null) {
                proxyPort = 1080;
            } else {
                proxyPort = Integer.parseInt(socksProxyPortStr);
            }
        }

        if (proxyHost != null && proxyPort != null) {
            proxy = new HttpHost(proxyHost, proxyPort);
        }
        return proxy;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        httpClient.close();
    }
}
