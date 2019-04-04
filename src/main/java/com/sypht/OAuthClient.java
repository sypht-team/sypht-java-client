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
import org.json.*;

import java.io.IOException;

/**
 * Hello world!
 */
public class OAuthClient extends com.sypht.CloseableHttpClient {
    protected static String SYPHT_AUTH_ENDPOINT = "https://login.sypht.com/oauth/token";
    protected ResponseHandler<String> responseHandler;
    protected String clientId;
    protected String clientSecret;
    protected String oauthAudience;

    public OAuthClient() {
        super();
        clientId = System.getenv("OAUTH_CLIENT_ID");
        clientSecret = System.getenv("OAUTH_CLIENT_SECRET");
        if(clientId == null || clientSecret == null) {
            throw new RuntimeException("OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET environment variables must be set before running this process, exiting");
        }
        oauthAudience = System.getenv("OAUTH_AUDIENCE");
        if(oauthAudience==null) {
            oauthAudience = "https://api.sypht.com";
        }
        this.responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    String responseBody = entity != null ? EntityUtils.toString(entity) : null;
                    JSONObject obj = new JSONObject(responseBody);
                    return obj.getString("access_token");
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
    }

    public String login() throws IOException {
        HttpPost httpPost = new HttpPost(SYPHT_AUTH_ENDPOINT);
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setHeader("Content-Type", "application/json");

        String json = "{" +
                "\"client_id\":\"" + clientId + "\"," +
                "\"client_secret\":\"" + clientSecret + "\"," +
                "\"audience\":\"" +  oauthAudience + "\"," +
                "\"grant_type\":\"client_credentials\"" +
                "}";
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        return httpClient.execute(httpPost, this.responseHandler);
    }

}
