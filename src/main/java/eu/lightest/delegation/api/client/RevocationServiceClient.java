package eu.lightest.delegation.api.client;

import java.net.URLEncoder;

import eu.lightest.delegation.api.DelegationAPIException;
import eu.lightest.delegation.api.util.HttpResponse;
import eu.lightest.delegation.api.util.NetClientUtil;

public class RevocationServiceClient extends AbstractServiceClient {

	public static void revokeDelegation(int id, String authToken, String reason) throws Exception {
		String serviceUrl = getDpRestServiceBaseUrl() + "/revoke/" + id + "/?token=" + authToken + "&reason=" + URLEncoder.encode(reason, "UTF-8");
		
		HttpResponse httpResponse = NetClientUtil.httpPost(serviceUrl);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
	}
}
