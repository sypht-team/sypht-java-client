package com.sypht;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connect to the Sypht API at https://api.sypht.com
 */
public class SyphtClient extends JsonResponseHandlerHttpClient {
    protected static int OAUTH_GRACE_PERIOD = 1000 * 60 * 15;
    protected static String SYPHT_API_ENDPOINT = "https://api.sypht.com";
    protected static Logger log = Logger.getLogger("com.sypht.SyphtClient");
    protected String bearerToken;
    protected OAuthClient oauthClient;

    /**
     * Create a default Sypht client that manages bearer tokens automatically.
     */
    public SyphtClient() {
        super();
        oauthClient = new OAuthClient();
    }

    /**
     * Create a custom Sypht client with your own bearer token.
     *
     * @param bearerToken the Jwt token
     */
    public SyphtClient(String bearerToken) {
        this();
        this.bearerToken = bearerToken;
    }

    /**
     * Pass a file to Sypht for detection.
     *
     * @param file the file in pdf, jpeg, gif or png format. Files may be up to
     *             20MB in size and pdf files may contain up to 16 individual pages.
     * @return a fileId as a String
     * @throws IOException in the event the upload went wrong.
     */
    public String upload(File file) throws IOException {
        return this.upload(file, null);
    }

    /**
     * Pass an inputStream to Sypht for detection.
     *
     * @param fileName the file name
     * @param inputStream    binary input stream of pdf, jpeg, gif or png format. Files may be up to
     *             20MB in size and pdf files may contain up to 16 individual pages.
     * @return a fileId as a String
     * @throws IOException in the event the upload went wrong.
     */
    public String upload(String fileName, InputStream inputStream) throws IOException {
        return this.upload(fileName, inputStream, null);
    }

    /**
     * Pass a file to Sypht for detection.
     *
     * @param file    the file in pdf, jpeg, gif or png format. Files may be up to
     *                20MB in size and pdf files may contain up to 16 individual pages.
     * @param options pass in custom upload options here.
     * @return a fileId as a String
     * @throws IOException in the event the upload went wrong.
     */
    public String upload(File file, Map<String, String> options) throws IOException {
        MultipartEntityBuilder builder = getMultipartEntityBuilderWithFile(file);
        parseOptions(options, builder);
        return performUpload(builder);
    }

    /**
     * Pass an inputStream to Sypht for detection.
     *
     * @param fileName the file name
     * @param inputStream    binary input stream of pdf, jpeg, gif or png format. Files may be up to
     *                20MB in size and pdf files may contain up to 16 individual pages.
     * @param options pass in custom upload options here.
     * @return a fileId as a String
     * @throws IOException in the event the upload went wrong.
     */
    public String upload(String fileName, InputStream inputStream, Map<String, String> options) throws IOException {
        MultipartEntityBuilder builder = getMultipartEntityBuilderWithInputStream(inputStream, fileName);
        parseOptions(options, builder);
        return performUpload(builder);
    }

    /**
     * Fetch prediction results from Sypht
     *
     * @param fileId the fileId
     * @return prediction results in JSON format.
     */
    public String result(String fileId) {
        HttpGet httpGet = createAuthorizedGet("/result/final/" + fileId);
        try {
            String result = httpClient.execute(httpGet, this.responseHandler);
            log.info("sypht results successfully fetched for fileId " + fileId);
            return result;
        } catch (Exception e) {
            log.log(Level.SEVERE, "error trying to get Sypht results for fileId " + fileId);
            throw new RuntimeException(e);
        }
    }

    protected String performUpload(MultipartEntityBuilder builder) throws IOException {
        HttpPost httpPost = createAuthorizedPost("/fileupload");
        httpPost.setEntity(builder.build());
        String fileId = this.execute(httpPost).getString("fileId");

        log.info("sypht file upload successful, fileId " + fileId);
        return fileId;
    }

    protected void parseOptions(Map<String, String> options, MultipartEntityBuilder builder) {
        if (options != null) {
            Iterator it = options.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry) it.next();
                builder.addTextBody(pair.getKey(), pair.getValue());
                it.remove();
            }
        }
    }

    protected MultipartEntityBuilder getMultipartEntityBuilderWithFile(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("fileToUpload", file, ContentType.APPLICATION_FORM_URLENCODED, file.getName());
        return builder;
    }

    protected MultipartEntityBuilder getMultipartEntityBuilderWithInputStream(InputStream inputStream, String fileName) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("fileToUpload", inputStream, ContentType.APPLICATION_FORM_URLENCODED, fileName);
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
