/*
 * CloseableHttpClient
 */
package com.sypht;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author Simon Mittag
 */
public abstract class CloseableHttpClient {
    protected static final int DEFAULT_HTTP_PROXY_PORT = 80;
    protected static final int DEFAULT_HTTPS_PROXY_PORT = 443;
    protected static final int DEFAULT_SOCKS_PROXY_PORT = 1080;
    protected org.apache.http.impl.client.CloseableHttpClient httpClient;

    public CloseableHttpClient() {

        HttpClientConnectionManager poolingConnManager
                = new PoolingHttpClientConnectionManager();

        HttpHost proxy = getSystemProxy();
        if (proxy!=null) {
            this.httpClient = HttpClients
                    .custom().setProxy(proxy)
                    .setConnectionManager(poolingConnManager)
                    .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
        } else {
            this.httpClient = HttpClients
                    .custom()
                    .setConnectionManager(poolingConnManager)
                    .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();
        }
    }

    private HttpHost getSystemProxy() {
        HttpHost proxy = null;
        String proxyHost = null;
        Integer proxyPort = null;
        String httpProxyHost = PropertyHelper.getEnvOrProperty("http.proxyHost");
        String httpsProxyHost = PropertyHelper.getEnvOrProperty("https.proxyHost");
        String socksProxyHost = PropertyHelper.getEnvOrProperty("socksProxyHost");

        if (httpProxyHost != null) {
            proxyHost = httpProxyHost;
            String httpProxyPortStr = PropertyHelper.getEnvOrProperty("http.proxyPort");
            if (httpProxyPortStr == null) {
                proxyPort = DEFAULT_HTTP_PROXY_PORT;
            } else {
                proxyPort = Integer.parseInt(httpProxyPortStr);
            }
        } else if (httpsProxyHost != null) {
            proxyHost = httpsProxyHost;
            String httpsProxyPortStr = PropertyHelper.getEnvOrProperty("https.proxyPort");
            if (httpsProxyPortStr == null) {
                proxyPort = DEFAULT_HTTPS_PROXY_PORT;
            } else {
                proxyPort = Integer.parseInt(httpsProxyPortStr);
            }
        } else if (socksProxyHost != null) {
            proxyHost = socksProxyHost;
            String socksProxyPortStr = PropertyHelper.getEnvOrProperty("socksProxyPort");
            if (socksProxyPortStr == null) {
                proxyPort = DEFAULT_SOCKS_PROXY_PORT;
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
