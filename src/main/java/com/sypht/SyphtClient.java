package com.sypht;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Connect to Sypht to upload files and retrieve result.
 */
public class SyphtClient {
    protected static String SYPHT_API_ENDPOINT = "https://api.sypht.com";
    protected CloseableHttpClient httpClient;
    protected ResponseHandler<String> jsonSuccessResponseHandler;
    protected String bearerToken;

    public SyphtClient() {
        this.httpClient = HttpClients.createDefault();
        this.jsonSuccessResponseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(
                    final HttpResponse response) throws ClientProtocolException, IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    System.out.println(response);
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
    }

    public SyphtClient(String bearerToken) {
        this();
        this.bearerToken=bearerToken;
    }

    public String upload(File file) throws IOException, ClientProtocolException {
        HttpPost httpPost = new HttpPost(SYPHT_API_ENDPOINT+"/fileupload/");
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + getBearerToken());

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("fileToUpload", file, ContentType.APPLICATION_FORM_URLENCODED, file.getName());

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);

        String responseBody = httpClient.execute(httpPost, this.jsonSuccessResponseHandler);
        JSONObject obj = new JSONObject(responseBody);
        return obj.getString("fileId");
    }

    protected String getBearerToken() {
        if(this.bearerToken==null) {
            try {
                this.bearerToken = new OAuthClient().login();
                return this.bearerToken;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            return this.bearerToken;
        }
    }

    public JSONObject result(String fileId) throws IOException {
        HttpGet httpGet = new HttpGet(SYPHT_API_ENDPOINT+"/result/final/" + fileId);
        httpGet.setHeader("Accepts", "application/json");
        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setHeader("Authorization", "Bearer " + getBearerToken());
        return new JSONObject(httpClient.execute(httpGet, this.jsonSuccessResponseHandler));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        httpClient.close();
    }
}
