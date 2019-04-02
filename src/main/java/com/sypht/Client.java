package com.sypht;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Hello world!
 */
public class Client {
    protected static String SYPHT_API_BASE_ENDPOINT = "https://api.sypht.com";
    protected static String SYPHT_AUTH_ENDPOINT = "https://login.sypht.com/oauth/token";
    protected static String SYPHT_OAUTH_COMPANY_ID_CLAIM_KEY = "https://api.sypht.com/companyId";


    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(SYPHT_AUTH_ENDPOINT);
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setHeader("Content-Type", "application/json");

        String json = "{" +
                "\"client_id\":\"" + System.getenv("OAUTH_CLIENT_ID") + "\"," +
                "\"client_secret\":\"" + System.getenv("OAUTH_CLIENT_SECRET") + "\"," +
                "\"audience\":\"" + System.getenv("OAUTH_AUDIENCE") + "\"," +
                "\"grant_type\":\"client_credentials\"" +
                "}";
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);


        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            @Override
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }

        };
        String responseBody = httpclient.execute(httpPost, responseHandler);
        System.out.println("----------------------------------------");
        System.out.println(responseBody);
    }
}
