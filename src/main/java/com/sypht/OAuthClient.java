package com.sypht;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

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
        clientId = System.getenv("OAUTH_CLIENT_ID");
        clientSecret = System.getenv("OAUTH_CLIENT_SECRET");
        if (clientId == null || clientSecret == null) {
            throw new RuntimeException("OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET environment" +
                    " variables must be set before running this process, exiting");
        }
        oauthAudience = System.getenv("OAUTH_AUDIENCE");
        if (oauthAudience == null) {
            oauthAudience = "https://api.sypht.com";
        }
    }

    /**
     * Get a JWT bearer token for use with the Sypht API in exchange for your
     * client id and secret.
     *
     * @return a bearer token as a String
     * @throws IOException when the client can't log-in to Sypht.
     */
    public String login() throws IOException {
        String json = "{" +
                "\"client_id\":\"" + clientId + "\"," +
                "\"client_secret\":\"" + clientSecret + "\"," +
                "\"audience\":\"" + oauthAudience + "\"," +
                "\"grant_type\":\"client_credentials\"" +
                "}";
        StringEntity entity = new StringEntity(json);

        HttpPost httpPost = new HttpPost(SYPHT_AUTH_ENDPOINT);
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(entity);

        JSONObject jsonResponse = this.execute(httpPost);
        log.info("successfully logged into Sypht for clientId " + clientId);
        return jsonResponse.getString("access_token");
    }
}
