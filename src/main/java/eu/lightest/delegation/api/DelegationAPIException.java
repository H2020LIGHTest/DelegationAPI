package eu.lightest.delegation.api;

import eu.lightest.delegation.api.util.HttpResponse;

public class DelegationAPIException extends Exception{

	private static final long serialVersionUID = -5342913811900469982L;
	
	private HttpResponse httpResponse;

	public DelegationAPIException(String message) {
        super(message);
    }
	
	public DelegationAPIException(String message, Throwable cause) {
        super(message, cause);
    }
	
	public DelegationAPIException(HttpResponse httpResponse) {
        super("ResponseCode: " + httpResponse.getResponseCode() + ", ResponseMessage: " + httpResponse.getResponseMessage());
        this.httpResponse = httpResponse;
    }
	
	public DelegationAPIException(HttpResponse httpResponse, Throwable cause) {
		super("ResponseCode: " + httpResponse.getResponseCode() + ", ResponseMessage: " + httpResponse.getResponseMessage(), cause);
        this.httpResponse = httpResponse;
    }
	
	public HttpResponse getHttpResponse() {
		return httpResponse;
	}
}
