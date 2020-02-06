package eu.lightest.delegation.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NetClientUtil {
	
	static {
		try {
			HttpsServerUtil.configureSSL();
		} catch (Exception e) {
			//do nothing
		}
	}

    private static INetClient netClientImpl = new NetClientOkHttp3Impl();

    public static void setNetClientImpl(INetClient netClientImpl) {
		NetClientUtil.netClientImpl = netClientImpl;
	}

	private NetClientUtil() {
	}

    public static HttpResponse httpGet(String urlString) throws IOException {
        return netClientImpl.httpGet(urlString, new HashMap<String, String>(0));
    }

    public static HttpResponse httpGet(String urlString, Map<String,String> httpHeaders) throws IOException {  
        return netClientImpl.httpGet(urlString, httpHeaders);
    }

    public static HttpResponse httpPut(String url, InputStream data) throws IOException {
        return netClientImpl.httpPut(url, IOUtil.toByteArray(data));
    }

    public static HttpResponse httpPut(String urlString, byte[] data) throws IOException {
        return netClientImpl.httpPut(urlString, data);
    }

    public static HttpResponse httpDelete(String urlString) throws IOException {
        return netClientImpl.httpDelete(urlString);
    }

    public static HttpResponse httpDelete(String url, InputStream data, INetClient.RequestContentType contentType) throws IOException {
        return netClientImpl.httpDelete(url, IOUtil.toByteArray(data), contentType);
    }

    public static HttpResponse httpDelete(String urlString, byte[] data, INetClient.RequestContentType contentType) throws IOException {
        return netClientImpl.httpDelete(urlString, data, contentType);
    }
    
    public static HttpResponse httpPost(String url) throws Exception {
        return netClientImpl.httpPost(url);
    }

    public static HttpResponse httpPost(String url, InputStream data, INetClient.RequestContentType contentType) throws IOException {
        return netClientImpl.httpPost(url, IOUtil.toByteArray(data), contentType);
    }

    public static HttpResponse httpPost(String urlString, byte[] data, INetClient.RequestContentType contentType) throws IOException {
        return netClientImpl.httpPost(urlString, data, new HashMap<String, String>(0), contentType);
    }

    public static HttpResponse httpPost(String urlString, byte[] data, Map<String,String> httpHeaders, INetClient.RequestContentType contentType) throws IOException {
        return netClientImpl.httpPost(urlString, data, httpHeaders, contentType);
    }

    public static boolean isResponseSucceed(int responseCode){
        return netClientImpl.isResponseSucceed(responseCode);
    }
}
