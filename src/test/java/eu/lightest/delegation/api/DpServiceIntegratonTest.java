package eu.lightest.delegation.api;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Date;
import java.util.List;

import eu.lightest.delegation.api.util.NetClientOkHttp3Impl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.lightest.delegation.api.client.AuthenticationServiceClient;
import eu.lightest.delegation.api.client.DiscoveryServiceClient;
import eu.lightest.delegation.api.client.DownloadServiceClient;
import eu.lightest.delegation.api.client.PublicationServiceClient;
import eu.lightest.delegation.api.client.model.DelegationDataSet;
import eu.lightest.delegation.api.client.model.DelegationKeyDataSet;
import eu.lightest.delegation.api.model.xsd.DelegationType;
import eu.lightest.delegation.api.model.xsd.DomainType;
import eu.lightest.delegation.api.model.xsd.ValidityType;
import eu.lightest.delegation.api.model.xsd.domain.Order;
import eu.lightest.delegation.api.util.IOUtil;
import eu.lightest.delegation.api.util.NetClientHttpURLConnectionImpl;
import eu.lightest.delegation.api.util.NetClientUtil;
import iaik.security.provider.IAIK;

public class DpServiceIntegratonTest {
	
	private static Log logger = LogFactory.getLog(DpServiceIntegratonTest.class);
	
	private static KeyPair mandatorKeypair;
	private static KeyPair proxyKeypair;
	private static KeyPair intermediaryKeypair;
	
	
	private static Integer delegationId = null;
	
	@BeforeClass
	public static void init() throws NoSuchAlgorithmException {
		
		//Security.addProvider(new BouncyCastleProvider());
		Security.addProvider(new IAIK());
		
		NetClientUtil.setNetClientImpl(new NetClientOkHttp3Impl());
				
		mandatorKeypair = DelegationApi.generateKeyPair();
        proxyKeypair = DelegationApi.generateKeyPair();
        intermediaryKeypair = DelegationApi.generateKeyPair();
        
		logger.info("Mandator PublicKey (base64)");
		logger.info(IOUtil.encodeBase64(mandatorKeypair.getPublic().getEncoded()));
		
		logger.info("Mandator PrivateKey (base64)");
		logger.info(IOUtil.encodeBase64(mandatorKeypair.getPrivate().getEncoded()));
		
		try {			
			Provider[] providers = Security.getProviders();
			for (int i = 0; i < providers.length; i++) {
				Provider provider = providers[i];
				logger.debug("provider[" + i + "] " + provider.getInfo());
			}
			
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			Assert.assertFalse(true);
		}
	}
	
	@Test 
	public void authenticationServiceTest() {
		try {
			String encodedChallenge = AuthenticationServiceClient.startAuthentication(mandatorKeypair.getPublic().getEncoded());
			logger.info("startAuthentication: ok( encodedChallenge = " + encodedChallenge + " )");
			
			String result = AuthenticationServiceClient.decodeChallenge(encodedChallenge, mandatorKeypair.getPrivate());
			logger.info("result: " + result);
			
			String token = AuthenticationServiceClient.endAuthentication(encodedChallenge, result);
			logger.info("endAuthentication: ok( token = " + token + " )");
		} catch (Exception e) {
			logger.error("authenticationServiceTest: nok: " + e.getMessage(), e);
			Assert.assertFalse(true);
		}
	}
	
	@Test
    public void publishServiceTest() {
		try {
			
			if(delegationId != null) {
    			logger.warn("Delegation already published (Currently only one delegation can be published by a user!)");
    			return;
    		}
				
			DelegationType delegationType = DelegationBuilder.buildDelegationType(DelegationBuilder.buildInformationType(1), new Date(), mandatorKeypair.getPublic().getEncoded(), proxyKeypair.getPublic().getEncoded(), null, true, true);
			
			ValidityType validityType = DelegationBuilder.buildValidityType(new Date(), new Date(System.currentTimeMillis() + 1000000));
			delegationType.setValidity(validityType);
			
			DomainType domainType = new DomainType();
			domainType.setName("order");
			domainType.setVersion("1.0");
	        
	        List<Object> domainValueList = domainType.getAny();
	        domainValueList.add(Order.createOrder(0));
	        delegationType.setDomain(domainType);
			
			String delegationXml = DelegationBuilder.buildDelegationXml(delegationType, new Class[] {Order.class});
			
			logger.info("delegationXml\n" + delegationXml);
	
			String signedDelegationXml = DelegationApi.signDelegationXml(delegationXml, mandatorKeypair.getPrivate(), mandatorKeypair.getPublic(), false);
			
			byte[] symmetricKey = DelegationApi.createSymmetricKey();
			logger.info("symmetricKey: " + IOUtil.encodeBase64(symmetricKey));
			
			String encryptedDelegationXML = DelegationApi.encryptDelegationXml(signedDelegationXml, symmetricKey);
			logger.info("encryptedDelegationXML: " + encryptedDelegationXML);
			
			String encryptedDelegationKey = DelegationApi.encryptSymmetricKey(symmetricKey, proxyKeypair.getPublic());
			logger.info("encryptedDelegationKey: " + encryptedDelegationKey);
			
			delegationId= PublicationServiceClient.publishDelegation(encryptedDelegationXML, proxyKeypair.getPublic().getEncoded());
			logger.info("publishDelegation: ok ( id = " + delegationId + " )");
			
			PublicationServiceClient.publishDelegationKey(delegationId, encryptedDelegationKey, proxyKeypair.getPublic().getEncoded());
			logger.info("publishDelegationKey: ok");
		} catch (Exception e) {
			logger.error("publishServiceTest: nok: " + e.getMessage(), e);
			Assert.assertFalse(true);
		}
	}
	
	@Test
    public void downloadAndDecryptDelegationTest() {
		try {
			
			if(delegationId == null)
				publishServiceTest();
			
			//authentication
			String encodedChallenge = AuthenticationServiceClient.startAuthentication(proxyKeypair.getPublic().getEncoded());
			logger.info("startAuthentication: ok( encodedChallenge = " + encodedChallenge + " )");
			
			String result = AuthenticationServiceClient.decodeChallenge(encodedChallenge, proxyKeypair.getPrivate());
			logger.info("result: " + result);
			
			String token = AuthenticationServiceClient.endAuthentication(encodedChallenge, result);
			logger.info("endAuthentication: ok( token = " + token + " )");
			
			//search delegation
    		DelegationDataSet delegationDataSet =  DiscoveryServiceClient.searchSpecificDelegation(delegationId, token);
    		
    		logger.info("DiscoveryServiceClient.searchSpecificDelegation: ok");

    		if(delegationDataSet == null) {
    			logger.warn("Any delegation not found");
    			return;
    		}
    		
    		//download delegation key
			DelegationKeyDataSet delegationKeyDataSet =  DownloadServiceClient.downloadKeyForDelegationId(delegationId, token);
    		
    		if(delegationKeyDataSet == null) {
    			logger.warn("Any delegation key not found");
    			return;
    		}
    		
    		logger.info("DownloadServiceClient.downloadKeyForDelegationId: ok");
    		
    		//Decrypt delegation XML
    		String encryptedSymmetricKey = delegationKeyDataSet.getKey();
    		
    		logger.info("encryptedSymmetricKey: " + encryptedSymmetricKey);
    		
    		byte[] decryptedSymmetricKey = DelegationApi.decryptSymmetricKey(encryptedSymmetricKey, proxyKeypair.getPrivate());
    		
    		logger.info("decryptedSymmetricKey: " + IOUtil.encodeBase64(decryptedSymmetricKey));
    		
    		logger.info("encryptedDelegationXML: " + delegationDataSet.getData());
    		
    		String decryptedDelegationXML = DelegationApi.decryptDelegationXml(delegationDataSet.getData(), decryptedSymmetricKey);
    		logger.info("downloadAndDecryptDelegationTest: ok");
    		logger.info(decryptedDelegationXML);
		} catch (Exception e) {
			logger.error("downloadAndDecryptDelegationTest: nok: " + e.getMessage(), e);
			Assert.assertFalse(true);
		}
	}
}
