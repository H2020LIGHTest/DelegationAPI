package eu.lightest.delegation.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetClientOkHttp3Impl implements INetClient {

    private static OkHttpClient mClient = new OkHttpClient();

    public NetClientOkHttp3Impl() {
	}

    public HttpResponse httpGet(String urlString) throws IOException {
        return httpGet(urlString, new HashMap<String, String>(0));
    }

    public HttpResponse httpGet(String urlString, Map<String,String> httpHeaders) throws IOException {
        Headers headerbuild = Headers.of(httpHeaders);
        Request req = new Request.Builder()
                .url(urlString)
                .headers(headerbuild)
                .get()
                .build();

        Response rsp = mClient.newCall(req).execute();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(rsp.code());
        httpResponse.setContent(rsp.body().string().getBytes());

        return httpResponse;
    }

    public HttpResponse httpPut(String url, InputStream data) throws IOException {
        return httpPut(url, IOUtil.toByteArray(data));
    }

    public HttpResponse httpPut(String urlString, byte[] data) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("Application/Json"), data);
        Request req = new Request.Builder()
                .url(urlString)
                .put(body)
                .build();

        Response rsp = mClient.newCall(req).execute();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(rsp.code());
        httpResponse.setContent(rsp.body().string().getBytes());

        return httpResponse;
    }

    public HttpResponse httpDelete(String urlString) throws IOException {
        Request req = new Request.Builder()
                .url(urlString)
                .delete()
                .build();

        Response rsp = mClient.newCall(req).execute();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(rsp.code());
        httpResponse.setContent(rsp.body().string().getBytes());

        return httpResponse;
    }

    public HttpResponse httpDelete(String url, InputStream data, RequestContentType contentType) throws IOException {
        return httpDelete(url, IOUtil.toByteArray(data), contentType);
    }

    public HttpResponse httpDelete(String urlString, byte[] data, RequestContentType contentType) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse(contentType.getValue()), data);
        Request req = new Request.Builder()
                .url(urlString)
                .delete(body)
                .build();

        Response rsp = mClient.newCall(req).execute();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(rsp.code());
        httpResponse.setContent(rsp.body().string().getBytes());

        return httpResponse;
    }
    
    public HttpResponse httpPost(String url) throws Exception {
        Request req = new Request.Builder()
                .url(url)
                .build();

        Response rsp = mClient.newCall(req).execute();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(rsp.code());
        httpResponse.setContent(rsp.body().string().getBytes());

        return httpResponse;
    }

    public HttpResponse httpPost(String url, InputStream data, RequestContentType contentType) throws IOException {
        return httpPost(url, IOUtil.toByteArray(data), contentType);
    }

    public HttpResponse httpPost(String urlString, byte[] data, RequestContentType contentType) throws IOException {
        return httpPost(urlString, data, new HashMap<String, String>(0), contentType);
    }

    public HttpResponse httpPost(String urlString, byte[] data, Map<String,String> httpHeaders, RequestContentType contentType) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse(contentType.getValue()), data);
        Request req = new Request.Builder()
                .url(urlString)
                .post(body)
                .build();

        Response rsp = mClient.newCall(req).execute();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setResponseCode(rsp.code());
        httpResponse.setContent(rsp.body().string().getBytes());

        return httpResponse;
    }

    public boolean isResponseSucceed(int responseCode){
        if (responseCode == HttpURLConnection.HTTP_OK ||
        		responseCode == HttpURLConnection.HTTP_CREATED ||
        		responseCode == HttpURLConnection.HTTP_ACCEPTED) {
            return true;
        }

        return false;
    }
}
