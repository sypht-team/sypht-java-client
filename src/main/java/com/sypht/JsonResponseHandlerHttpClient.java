/*
 * JsonResponseHandlerHttpClien
 */
package com.sypht;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Simon Mittag
 */
public abstract class JsonResponseHandlerHttpClient extends CloseableHttpClient {
    protected ResponseHandler<String> responseHandler;

    public JsonResponseHandlerHttpClient() {
        super();
        this.responseHandler = new ResponseHandler<String>() {
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
    }

    public JSONObject execute(HttpEntityEnclosingRequestBase httpRequest) throws IOException {
        return new JSONObject(httpClient.execute(httpRequest, this.responseHandler));
    }
}
