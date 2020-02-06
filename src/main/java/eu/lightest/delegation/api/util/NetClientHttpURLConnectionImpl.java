package eu.lightest.delegation.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NetClientHttpURLConnectionImpl implements INetClient{
	
	public NetClientHttpURLConnectionImpl() {
	}

    public HttpResponse httpGet(String urlString) throws IOException {
        return httpGet(urlString, new HashMap<String, String>(0));
    }

    public HttpResponse httpGet(String urlString, Map<String,String> httpHeaders) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            for(String headerKey: httpHeaders.keySet())
                conn.setRequestProperty(headerKey, httpHeaders.get(headerKey));

            httpResponse.setResponseCode(conn.getResponseCode());
            httpResponse.setResponseMessage(conn.getResponseMessage());

            /**
            if (!isResponseSucceed(httpResponse.getResponseCode())) {
                return httpResponse;
            }*/

            try {
                InputStream is = null;
                if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    is = conn.getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                
                if(conn.getContentEncoding() != null && conn.getContentEncoding().equalsIgnoreCase("gzip")){
                    httpResponse.setContent(IOUtil.decompressGziptoByteArray(is));
                }else{
                    httpResponse.setContent(IOUtil.toByteArray(is));
                }
            }catch(Exception e){
                //do nothing
            }

            return httpResponse;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public HttpResponse httpPut(String url, InputStream data) throws IOException {
        return httpPut(url, IOUtil.toByteArray(data));
    }

    public HttpResponse httpPut(String urlString, byte[] data) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");

            OutputStream os = conn.getOutputStream();
            os.write(data);
            os.flush();

            httpResponse.setResponseCode(conn.getResponseCode());
            httpResponse.setResponseMessage(conn.getResponseMessage());

            /**
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return httpResponse;
            }
             */

            try {
                InputStream is = null;
                if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    is = conn.getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                
                if(conn.getContentEncoding() != null && conn.getContentEncoding().equalsIgnoreCase("gzip")){
                    httpResponse.setContent(IOUtil.decompressGziptoByteArray(is));
                }else{
                    httpResponse.setContent(IOUtil.toByteArray(is));
                }
            }catch(Exception e){
                //do nothing
            }

            return httpResponse;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    public HttpResponse httpDelete(String urlString) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            httpResponse.setResponseCode(conn.getResponseCode());
            httpResponse.setResponseMessage(conn.getResponseMessage());

            /**
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return httpResponse;
            }*/

            try {
                InputStream is = null;
                if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    is = conn.getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                
                if(conn.getContentEncoding() != null && conn.getContentEncoding().equalsIgnoreCase("gzip")){
                    httpResponse.setContent(IOUtil.decompressGziptoByteArray(is));
                }else{
                    httpResponse.setContent(IOUtil.toByteArray(is));
                }
            }catch(Exception e){
                //do nothing
            }

            return httpResponse;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public HttpResponse httpDelete(String url, InputStream data, RequestContentType contentType) throws IOException {
        return httpDelete(url, IOUtil.toByteArray(data), contentType);
    }

    public HttpResponse httpDelete(String urlString, byte[] data, RequestContentType contentType) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", contentType.getValue());

            if(data != null){
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(data);
                os.flush();
            }

            httpResponse.setResponseCode(conn.getResponseCode());
            httpResponse.setResponseMessage(conn.getResponseMessage());

            /**
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return httpResponse;
            }
             */

            try {
                InputStream is = null;
                if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    is = conn.getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                
                if(conn.getContentEncoding() != null && conn.getContentEncoding().equalsIgnoreCase("gzip")){
                    httpResponse.setContent(IOUtil.decompressGziptoByteArray(is));
                }else{
                    httpResponse.setContent(IOUtil.toByteArray(is));
                }
            }catch(Exception e){
                //do nothing
            }

            return httpResponse;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    public HttpResponse httpPost(String url) throws Exception {
        return httpPost(url, null, new HashMap<String, String>(0), null);
    }

    public HttpResponse httpPost(String url, InputStream data, RequestContentType contentType) throws IOException {
        return httpPost(url, IOUtil.toByteArray(data), contentType);
    }

    public HttpResponse httpPost(String urlString, byte[] data, RequestContentType contentType) throws IOException {
        return httpPost(urlString, data, new HashMap<String, String>(0), contentType);
    }

    public HttpResponse httpPost(String urlString, byte[] data, Map<String,String> httpHeaders, RequestContentType contentType) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            
            if(contentType != null)
            	conn.setRequestProperty("Content-Type", contentType.getValue());

            if(httpHeaders != null) {
            	for(String headerKey: httpHeaders.keySet())
                    conn.setRequestProperty(headerKey, httpHeaders.get(headerKey));
            }
            
            if(data != null) {
            	conn.setRequestProperty("Content-Length", Integer.toString(data.length));
            	OutputStream os = conn.getOutputStream();
                os.write(data);
                os.flush();
            }

            httpResponse.setResponseCode(conn.getResponseCode());
            httpResponse.setResponseMessage(conn.getResponseMessage());

            /**
            if (!isResponseSucceed(httpResponse.getResponseCode())) {
                return httpResponse;
            }
             */

            try {
                InputStream is = null;
                if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    is = conn.getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                
                if(conn.getContentEncoding() != null && conn.getContentEncoding().equalsIgnoreCase("gzip")){
                    httpResponse.setContent(IOUtil.decompressGziptoByteArray(is));
                }else{
                    httpResponse.setContent(IOUtil.toByteArray(is));
                }
            }catch(Exception e){
                //do nothing
            }

            return httpResponse;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
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
