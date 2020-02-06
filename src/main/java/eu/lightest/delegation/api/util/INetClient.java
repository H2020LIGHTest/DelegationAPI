package eu.lightest.delegation.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface INetClient {
	
	public enum RequestContentType {
		
	    XML("application/xml"),
	    JSON("application/json"),
		FORM("application/x-www-form-urlencoded"),
		PLAIN_TEXT("plain/text"),
		;
		
	    private String value;


	    RequestContentType(String value) {
	        this.value = value;
	    }

	    /**
	     * @return the value
	     */
	    public String getValue() {
	        return value;
	    }
	}
	
	public HttpResponse httpGet(String urlString) throws IOException;
	
	public HttpResponse httpGet(String urlString, Map<String,String> httpHeaders) throws IOException;
	
	public HttpResponse httpPut(String url, InputStream data) throws IOException;
	
	public HttpResponse httpPut(String urlString, byte[] data) throws IOException;
	
	public HttpResponse httpDelete(String urlString) throws IOException;

	public HttpResponse httpDelete(String url, InputStream data, RequestContentType contentType) throws IOException;
	
	public HttpResponse httpDelete(String urlString, byte[] data, RequestContentType contentType) throws IOException;
	
	public HttpResponse httpPost(String url) throws Exception;

    public HttpResponse httpPost(String url, InputStream data, RequestContentType contentType) throws IOException;

    public HttpResponse httpPost(String urlString, byte[] data, RequestContentType contentType) throws IOException;

    public HttpResponse httpPost(String urlString, byte[] data, Map<String,String> httpHeaders, RequestContentType contentType) throws IOException;

    public boolean isResponseSucceed(int responseCode);
}
