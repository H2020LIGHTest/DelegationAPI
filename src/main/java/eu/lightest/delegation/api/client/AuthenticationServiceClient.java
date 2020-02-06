package eu.lightest.delegation.api.client;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import eu.lightest.delegation.api.DelegationAPIException;
import eu.lightest.delegation.api.util.HttpResponse;
import eu.lightest.delegation.api.util.INetClient;
import eu.lightest.delegation.api.util.IOUtil;
import eu.lightest.delegation.api.util.NetClientUtil;
import iaik.security.provider.IAIK;

public class AuthenticationServiceClient extends AbstractServiceClient {
		
	private AuthenticationServiceClient() {
	}
	
	/**
	 * 
	 * @param publicKey
	 * @return encoded challenge
	 * @throws DelegationAPIException
	 * @throws IOException 
	 */
	public static String startAuthentication(byte[] publicKey) throws DelegationAPIException, IOException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/auth";

		HttpResponse httpResponse = NetClientUtil.httpPost(serviceUrl, IOUtil.encodeBase64(publicKey).getBytes(), INetClient.RequestContentType.PLAIN_TEXT);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
		
		return httpResponse.getContent() == null ? null : new String(httpResponse.getContent());
	}
	
	/**
	 * 
	 * @param publicKey
	 * @param result
	 * @return authentication token
	 * @throws DelegationAPIException
	 * @throws IOException 
	 */
	public static String endAuthentication(String publicKey, String result) throws DelegationAPIException, IOException {
		String serviceUrl = getDpRestServiceBaseUrl() + "/auth/result";
		
		String formParameters = "key=" + publicKey + "&result=" + result;
		HttpResponse httpResponse = NetClientUtil.httpPost(serviceUrl, formParameters.getBytes(), INetClient.RequestContentType.FORM);
		if(!NetClientUtil.isResponseSucceed(httpResponse.getResponseCode()))
			throw new DelegationAPIException(httpResponse);
		
		return httpResponse.getContent() == null ? null : new String(httpResponse.getContent());
	}
	
	/**
	 * 
	 * @param encodedChallenge
	 * @param privateKey
	 * @return decode encodedChallenge that is returned from startAuthentication method
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public static String decodeChallenge(String encodedChallenge, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", new IAIK());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] byteChallenge = IOUtil.decodeBase64(encodedChallenge);

        byte[] result = cipher.doFinal(byteChallenge);

        byte[] num = new byte[4];
        num[0] = result[result.length-4];
		num[1] = result[result.length-3];
		num[2] = result[result.length-2];
		num[3] = result[result.length-1];

		return new String(num);
    }

}
