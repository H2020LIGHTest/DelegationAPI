package eu.lightest.delegation.api.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.lightest.delegation.api.DelegationAPIException;
import eu.lightest.delegation.api.util.HttpResponse;
import eu.lightest.delegation.api.util.INetClient;
import eu.lightest.delegation.api.util.IOUtil;
import eu.lightest.delegation.api.util.NetClientUtil;

public class PublicationServiceClient extends AbstractServiceClient {
	
	private PublicationServiceClient() {
	}


	public static int publishDelegation(String encryptedDelegationXML, byte[] publicKey) throws DelegationAPIException, IOException, NoSuchAlgorithmException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/publish";
		
		String jsonRequest = "{delegation:\"" + encryptedDelegationXML +
				             "\", public_key:\"" + IOUtil.encodeBase64(publicKey) +
				             "\", public_key_hash:\"" + IOUtil.encodeBase32(IOUtil.calculateHash(publicKey)) +
							 "\"}";
		
		HttpResponse httpResponse = NetClientUtil.httpPost(serviceUrl, jsonRequest.getBytes(), INetClient.RequestContentType.JSON);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
		
		JsonParser parser = new JsonParser();
		JsonObject jsonResponse = parser.parse(new String(httpResponse.getContent())).getAsJsonObject();
		return jsonResponse.getAsJsonPrimitive("id").getAsInt();
	}
	
	public static void publishDelegationKey(int id, String encryptedDelegationKey, byte[] publicKey) throws DelegationAPIException, IOException, NoSuchAlgorithmException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/publish_key";
		
		String jsonRequest = "{id:\"" + id +
							 "\", key:\"" + encryptedDelegationKey +
							 "\", public_key_hash:\"" + IOUtil.encodeBase32(IOUtil.calculateHash(publicKey)) +
							 "\"}";
		
		HttpResponse httpResponse = NetClientUtil.httpPost(serviceUrl, jsonRequest.getBytes(), INetClient.RequestContentType.JSON);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
	}
}
