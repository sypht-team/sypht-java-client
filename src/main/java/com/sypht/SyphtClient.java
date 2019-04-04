package com.sypht;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Connect to Sypht to upload files and retrieve result.
 */
public class SyphtClient extends JsonResponseHandlerHttpClient {
    protected static String SYPHT_API_ENDPOINT = "https://api.sypht.com";
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
        return this.execute(httpPost).getString("fileId");
    }

    public JSONObject result(String fileId) throws IOException {
        HttpGet httpGet = createAuthorizedGet("/result/final/" + fileId);
        return new JSONObject(httpClient.execute(httpGet, this.responseHandler));
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

    protected String getBearerToken() {
        if (this.bearerToken == null) {
            try {
                this.bearerToken = oauthClient.login();
                return this.bearerToken;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            return this.bearerToken;
        }
    }
}
