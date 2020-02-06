package eu.lightest.delegation.api.client;

import java.io.IOException;

import com.google.gson.Gson;

import eu.lightest.delegation.api.DelegationAPIException;
import eu.lightest.delegation.api.client.model.DelegationKeyDataSet;
import eu.lightest.delegation.api.client.model.JsonDownloadDelegation;
import eu.lightest.delegation.api.util.HttpResponse;
import eu.lightest.delegation.api.util.NetClientUtil;

public class DownloadServiceClient extends AbstractServiceClient {

	private DownloadServiceClient() {
	}
	
	public static JsonDownloadDelegation downloadDelegationWithId(int id, String authToken) throws DelegationAPIException, IOException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/download/" + id + "/?token=" + authToken;
		
		HttpResponse httpResponse = NetClientUtil.httpGet(serviceUrl);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
		
		Gson gson = new Gson();
        return gson.fromJson(new String(httpResponse.getContent()), JsonDownloadDelegation.class);
	}
	
	public static DelegationKeyDataSet downloadKeyForDelegationId(int id, String authToken) throws DelegationAPIException, IOException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/download/" + id + "/key?token=" + authToken;
		
		HttpResponse httpResponse = NetClientUtil.httpGet(serviceUrl);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
		
		Gson gson = new Gson();
        return gson.fromJson(new String(httpResponse.getContent()), DelegationKeyDataSet.class);
	}
}
