package com.sypht;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * Log-in to the Sypht API
 */
public class OAuthClient extends JsonResponseHandlerHttpClient {
    protected static String SYPHT_AUTH_ENDPOINT = "https://login.sypht.com/oauth/token";
    protected static Logger log = Logger.getLogger("com.sypht.OAuthClient");

    protected String clientId;
    protected String clientSecret;
    protected String oauthAudience;


    /**
     * Create a default OAuthClient. Requires OAUTH_CLIENT_ID and OAUTH_CLIENT_SECRET
     * set as environment variables.
     */
    public OAuthClient() {
        super();
        clientId = PropertyHelper.getEnvOrProperty("OAUTH_CLIENT_ID");
        clientSecret = PropertyHelper.getEnvOrProperty("OAUTH_CLIENT_SECRET");
        if (clientId == null && clientSecret == null) {
            String syphtApiKey = PropertyHelper.getEnvOrProperty("SYPHT_API_KEY");
            if (syphtApiKey != null) {
                clientId = syphtApiKey.split(":")[0];
                clientSecret = syphtApiKey.split(":")[1];
            }
        }
        if (clientId == null || clientSecret == null) {
            throw new RuntimeException("SYPHT_API_KEY -OR- OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET environment" +
                    " variables must be set before running this process, exiting");
        }
        oauthAudience = PropertyHelper.getEnvOrProperty("OAUTH_AUDIENCE");
        if (oauthAudience == null) {
            oauthAudience = "https://api.sypht.com";
        }
    }

    private String authAuth0() throws IOException {
        String json = "{" +
                "\"client_id\":\"" + clientId + "\"," +
                "\"client_secret\":\"" + clientSecret + "\"," +
                "\"audience\":\"" + oauthAudience + "\"," +
                "\"grant_type\":\"client_credentials\"" +
                "}";
        StringEntity entity = new StringEntity(json);

        HttpPost httpPost = new HttpPost(getAuthenticationEndpoint());
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(entity);

        JSONObject jsonResponse = this.execute(httpPost);
        log.info("successfully logged into Sypht for clientId " + clientId);
        return jsonResponse.getString("access_token");
    }

    private String authCognito() throws IOException {
        String apiKey = clientId + ":" + clientSecret;
        String authorizationToken = Base64.getEncoder().encodeToString(apiKey.getBytes());

        List <NameValuePair> formParams = new ArrayList <NameValuePair>();
        formParams.add(new BasicNameValuePair("client_id", clientId));
        formParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
        
        HttpPost httpPost = new HttpPost(getAuthenticationEndpoint());
        httpPost.setHeader("Authorization", "Basic " + authorizationToken);
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setEntity(new UrlEncodedFormEntity(formParams, HTTP.UTF_8));

        JSONObject jsonResponse = this.execute(httpPost);
        log.info("successfully logged into Sypht for clientId " + clientId);
        return jsonResponse.getString("access_token");
    }

    private String getAuthenticationEndpoint() {
        String authenticationEndpoint = PropertyHelper.getEnvOrProperty("SYPHT_AUTH_ENDPOINT");
        if (authenticationEndpoint != null) {
            return authenticationEndpoint;
        }
        return SYPHT_AUTH_ENDPOINT;
    }

    /**
     * Get a JWT bearer token for use with the Sypht API in exchange for your
     * client id and secret.
     *
     * @return a bearer token as a String
     * @throws IOException when the client can't log-in to Sypht.
     */
    public String login() throws IOException {
        if (getAuthenticationEndpoint().contains("/oauth2/token")) {
            return authCognito();
        } else {
            return authAuth0();
        }
    }
}
