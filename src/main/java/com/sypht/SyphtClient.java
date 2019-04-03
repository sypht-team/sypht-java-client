package com.sypht;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Connect to Sypht to upload files and retrieve results.
 */
public class SyphtClient {
    protected static String SYPHT_API_ENDPOINT = "https://api.sypht.com";
    protected CloseableHttpClient httpClient;
    protected ResponseHandler<String> responseHandler;
    protected String bearerToken;

    public SyphtClient(String bearerToken) {
        this.bearerToken=bearerToken;
        this.httpClient = HttpClients.createDefault();
        this.responseHandler = new ResponseHandler<String>() {
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

    public String upload(File file) throws IOException, ClientProtocolException {
        HttpPost httpPost = new HttpPost(SYPHT_API_ENDPOINT+"/fileupload/");
        httpPost.setHeader("Accepts", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + bearerToken);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("fileToUpload", file, ContentType.APPLICATION_FORM_URLENCODED, file.getName());

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);

        String responseBody = httpClient.execute(httpPost, this.responseHandler);
        JSONObject obj = new JSONObject(responseBody);
        return obj.getString("fileId");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        httpClient.close();
    }
}
