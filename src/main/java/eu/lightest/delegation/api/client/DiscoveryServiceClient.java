package eu.lightest.delegation.api.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;

import eu.lightest.delegation.api.DelegationAPIException;
import eu.lightest.delegation.api.client.model.DelegationDataSet;
import eu.lightest.delegation.api.client.model.JsonDelegationResult;
import eu.lightest.delegation.api.util.HttpResponse;
import eu.lightest.delegation.api.util.IOUtil;
import eu.lightest.delegation.api.util.NetClientUtil;

public class DiscoveryServiceClient extends AbstractServiceClient {
	
	private DiscoveryServiceClient() {
	}

	public static JsonDelegationResult searchDelegations(byte[] publicKey, String authToken) throws DelegationAPIException, IOException, NoSuchAlgorithmException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/search/?hash=" + IOUtil.encodeBase64(IOUtil.calculateHash(publicKey)) + "&token=" + authToken;
		
		HttpResponse httpResponse = NetClientUtil.httpGet(serviceUrl);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
		
		try {
			Gson gson = new Gson();
	        return gson.fromJson(new String(httpResponse.getContent()), JsonDelegationResult.class);
		}catch (Exception e) {
			throw new DelegationAPIException(new String(httpResponse.getContent()), e);
		}
	}

	public static DelegationDataSet searchSpecificDelegation(int id, String authToken) throws DelegationAPIException, IOException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/search/" + id + "/?token=" + authToken;
		
		HttpResponse httpResponse = NetClientUtil.httpGet(serviceUrl);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
		
		Gson gson = new Gson();
        return gson.fromJson(new String(httpResponse.getContent()), DelegationDataSet.class);
	}
}
