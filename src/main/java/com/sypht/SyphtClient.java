package com.sypht;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Connect to the Sypht API at https://api.sypht.com
 */
public class SyphtClient extends JsonResponseHandlerHttpClient {
    protected static int OAUTH_GRACE_PERIOD = 1000 * 60 * 15;
    protected static String SYPHT_API_ENDPOINT = "https://api.sypht.com";
    protected static org.apache.log4j.Logger log =
            Logger.getLogger(SyphtClient.class);
    protected String bearerToken;
    protected OAuthClient oauthClient;

    public SyphtClient() {
        super();
        oauthClient = new OAuthClient();
    }

    public SyphtClient(String bearerToken) {
        this();
        this.bearerToken = bearerToken;
    }

    public String upload(File file) throws IOException {
        HttpPost httpPost = createAuthorizedPost("/fileupload");
        httpPost.setEntity(getMultipartEntityBuilderWithFile(file).build());
        return this.execute(httpPost).getString("fileId");
    }

    public String upload(File file, Map<String, String> options) throws IOException {
        MultipartEntityBuilder builder = getMultipartEntityBuilderWithFile(file);

        Iterator it = options.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry) it.next();
            builder.addTextBody(pair.getKey(), pair.getValue());
            it.remove();
        }

        HttpPost httpPost = createAuthorizedPost("/fileupload");
        httpPost.setEntity(builder.build());
        String fileId = this.execute(httpPost).getString("fileId");

        log.info("sypht file upload successful, fileId " + fileId + " for file " + file.getName());
        return fileId;
    }

    public String result(String fileId, Map<String, String>...options) throws IOException {
        HttpGet httpGet = createAuthorizedGet("/result/final/" + fileId);
        try {
            String result = httpClient.execute(httpGet, this.responseHandler);
            log.info("sypht results successfully fetched for fileId " + fileId);
            return result;
        } catch (Exception e) {
            log.error("error trying to get Sypht results for fileId " + fileId);
            throw new RuntimeException(e);
        }
    }

    protected MultipartEntityBuilder getMultipartEntityBuilderWithFile(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("fileToUpload", file, ContentType.APPLICATION_FORM_URLENCODED, file.getName());
        return builder;
    }

    protected HttpGet createAuthorizedGet(String slug) {
        HttpGet httpGet = new HttpGet(SYPHT_API_ENDPOINT + slug);
        httpGet.setHeader("Accepts", "application/json");
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Authorization", "Bearer " + getBearerToken());
        return httpGet;
    }

    protected HttpPost createAuthorizedPost(String slug) {
        HttpPost httpPost = new HttpPost(SYPHT_API_ENDPOINT + slug);
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + getBearerToken());
        return httpPost;
    }

    protected synchronized String getBearerToken() {
        if (this.bearerToken == null) {
            try {
                this.bearerToken = oauthClient.login();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            long cacheExpiry = cacheExpiry(decodeTokenClaims(this.bearerToken));
            if (cacheExpiry <= new Date().getTime()) {
                this.bearerToken = null;
                try {
                    this.bearerToken = oauthClient.login();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return this.bearerToken;
    }


    protected long cacheExpiry(Claims claims) {
        return claims.getExpiration().getTime() - OAUTH_GRACE_PERIOD;
    }

    protected Claims decodeTokenClaims(String token) {
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";

        Jwt<?, ?> jwt = Jwts.parser().parse(unsignedToken);
        Claims claims = (Claims) jwt.getBody();
        return claims;
    }
}
