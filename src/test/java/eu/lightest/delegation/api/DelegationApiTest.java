package eu.lightest.delegation.api;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
//import java.security.Security;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.JAXBException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import eu.lightest.delegation.api.model.xsd.DelegationType;
import eu.lightest.delegation.api.model.xsd.DomainType;
import eu.lightest.delegation.api.model.xsd.SignatureType;
import eu.lightest.delegation.api.model.xsd.ValidityType;
import eu.lightest.delegation.api.model.xsd.domain.Order;
import eu.lightest.delegation.api.util.IOUtil;

public class DelegationApiTest {
	
	private static Log logger = LogFactory.getLog(DelegationApiTest.class);
	
	private static KeyPair mandatorKeypair;
	private static KeyPair proxyKeypair;
	private static KeyPair intermediaryKeypair;
	
	@BeforeClass
	public static void init() throws NoSuchAlgorithmException {
		
        mandatorKeypair = DelegationApi.generateKeyPair();
        proxyKeypair = DelegationApi.generateKeyPair();
        intermediaryKeypair = DelegationApi.generateKeyPair();
	}
	
	@Test
    public void encryptDecryptDelegationXmlTest() throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		String data = "Hello world";
		
		byte[] symmetricKey = DelegationApi.createSymmetricKey();
		
		String encryptedData = DelegationApi.encryptDelegationXml(data, symmetricKey);
		String decryptedData = DelegationApi.decryptDelegationXml(encryptedData, symmetricKey);
		
		assertEquals(data, decryptedData);
	}
	
	@Test
    public void encryptDecryptSymmetricKeyTest() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] symmetricKey = DelegationApi.createSymmetricKey();
		
		String encryptedKey = DelegationApi.encryptSymmetricKey(symmetricKey, proxyKeypair.getPublic());
		byte[] decryptedKey = DelegationApi.decryptSymmetricKey(encryptedKey, proxyKeypair.getPrivate());
		
		assertArrayEquals(symmetricKey, decryptedKey);
	}
	
	@Test
    public void signValidateDelegationXmlTest() throws DatatypeConfigurationException, JAXBException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException, ParserConfigurationException, SAXException, IOException, MarshalException, XMLSignatureException, TransformerException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DelegationAPIException {
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
		logger.info(delegationXml);
		
		String signedDelegationXml = DelegationApi.signDelegationXml(delegationXml, mandatorKeypair.getPrivate(), mandatorKeypair.getPublic(), false);
		logger.info(signedDelegationXml);
		
		byte[] symmetricKey = DelegationApi.createSymmetricKey();
		String encryptedDelegationXML = DelegationApi.encryptDelegationXml(signedDelegationXml, symmetricKey);
		
		String encryptedKey = DelegationApi.encryptSymmetricKey(symmetricKey, proxyKeypair.getPublic());
		
		logger.info("encryptedDelegationXML base64");
		logger.info(IOUtil.encodeBase64(encryptedDelegationXML.getBytes()));
		
		logger.info("PrivateKey base64");
		logger.info(IOUtil.encodeBase64(mandatorKeypair.getPrivate().getEncoded()));
		
		logger.info("PublicKey base64");
		logger.info(IOUtil.encodeBase64(mandatorKeypair.getPublic().getEncoded()));
		
		logger.info("PublicKey hash base64");
		logger.info(IOUtil.encodeBase64(IOUtil.calculateHash(mandatorKeypair.getPublic().getEncoded())));
		
		logger.info("encryptedKey base64");
		logger.info(IOUtil.encodeBase64(encryptedKey.getBytes()));
		
		assertTrue(DelegationApi.verifyDelegationXml(signedDelegationXml, mandatorKeypair.getPublic(), false));
		
		//regenerate xml (signature verification fails)
		/**
		DelegationType mandatorDelegationType = (DelegationType) DelegationParser.parseDelegationXml(signedDelegationXml, new Class[] {Order.class});         
        String mandatorDelegationXml = DelegationBuilder.buildDelegationXml(mandatorDelegationType, new Class[] {Order.class});
        mandatorDelegationXml = mandatorDelegationXml.replace(" standalone=\"yes\"", "");
		logger.info(mandatorDelegationXml);
		*/
		
		/**
		//verify mandator delegation
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(new ByteArrayInputStream(mandatorDelegationXml.getBytes()));
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        document.getDocumentElement().normalize();
        
        document.getAttributes().removeNamedItem("standalone");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(document), new StreamResult(bos));
        logger.info(bos.toString());
        */
		
		//assertTrue(DelegationApi.verifyDelegationXml(mandatorDelegationXml, mandatorKeypair.getPublic(), false));
	}
	
	@SuppressWarnings("restriction")
	//@Test
    public void signValidateDelegationXmlTest2() throws DatatypeConfigurationException, JAXBException, ParserConfigurationException, SAXException, IOException, DelegationAPIException, MarshalException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException, XMLSignatureException, TransformerException {
		//mandator delegates
		DelegationType delegationType = DelegationBuilder.buildDelegationType(DelegationBuilder.buildInformationType(1), new Date(), mandatorKeypair.getPublic().getEncoded(), null, intermediaryKeypair.getPublic().getEncoded(), true, true);
		
		ValidityType validityType = DelegationBuilder.buildValidityType(new Date(), new Date(System.currentTimeMillis() + 1000000));
		delegationType.setValidity(validityType);
		
		DomainType domainType = new DomainType();
		domainType.setName("order");
		domainType.setVersion("1.0");
        
        List<Object> domainValueList = domainType.getAny();
        domainValueList.add(Order.createOrder(0));
        delegationType.setDomain(domainType);
		
		String delegationXml = DelegationBuilder.buildDelegationXml(delegationType, new Class[] {Order.class});
		logger.info(delegationXml);
		
		String signedDelegationXml = DelegationApi.signDelegationXml(delegationXml, mandatorKeypair.getPrivate(), mandatorKeypair.getPublic(), false);
		logger.info(signedDelegationXml);
		
		assertTrue(DelegationApi.verifyDelegationXml(signedDelegationXml, mandatorKeypair.getPublic(), false));
	
		//issuer delegates
        DelegationType newDelegationType = (DelegationType) DelegationParser.parseDelegationXml(signedDelegationXml, new Class[] {Order.class});         
        newDelegationType.setProxy(proxyKeypair.getPublic().getEncoded());
        SignatureType mandatorSignature = newDelegationType.getSignature();
        newDelegationType.setSignature(null);
        
        String newDelegationXml = DelegationBuilder.buildDelegationXml(newDelegationType, new Class[] {Order.class});
		logger.info(newDelegationXml);
        
		String signedDelegationXml2 = DelegationApi.signDelegationXml(newDelegationXml, intermediaryKeypair.getPrivate(), intermediaryKeypair.getPublic(), true);
		logger.info(signedDelegationXml2);
		
		assertTrue(DelegationApi.verifyDelegationXml(signedDelegationXml2, intermediaryKeypair.getPublic(), true));

		//verify mandator delegation
        DelegationType mandatorDelegationType = (DelegationType) DelegationParser.parseDelegationXml(signedDelegationXml2, new Class[] {Order.class});         
        mandatorDelegationType.setProxy(null);
        mandatorDelegationType.setSignature(mandatorSignature);
        
        String mandatorDelegationXml = DelegationBuilder.buildDelegationXml(mandatorDelegationType, new Class[] {Order.class});
		logger.info(mandatorDelegationXml);
		
		assertTrue(DelegationApi.verifyDelegationXml(mandatorDelegationXml, mandatorKeypair.getPublic(), false));
	}
	
	@SuppressWarnings("restriction")
	//@Test
	public void signValidateDelegationXmlTest3() throws ParserConfigurationException, SAXException, IOException, DelegationAPIException, MarshalException {
		
		PublicKey mandatorPublicKey = getKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk0Wt08aPrUmhP+l+hfXfLDzMiRmZKeUYokzrxq7ybfsPN2OxElgcXud0tq8ktpMRzmh7Aua6y4AFU4QZjx9oR9W06rRaBO5fLWTOojjHNKoJ7ujRi3u8BBwNGTdmScGiN0rDsbd6k4b1OCOBJnW9pgcxQHTE5j9TiLoQvPRJd7amp7svTFr1jyfTmCHVbJ0Wmu8GByEAVz9Gl5tozn/Q3Ce0ZJiyPDOZPaK2GBjP9kLxBnxdB36o6zc7IRR39liioU+MoRJLLq/FD6hO3o0b+Mbt1t6LR6aTta0j0UThRby5Ajrhaq9tpQ+qyErlI2nFfCAPQSorauFMtgG4sXmeaQIDAQAB");
		
		String delegation1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><delegation xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">\n" + 
				"    <information>\n" + 
				"        <version>1.0</version>\n" + 
				"        <sequence>1</sequence>\n" + 
				"    </information>\n" + 
				"    <issuedDate>2019-04-09T11:37:50.714+03:00</issuedDate>\n" + 
				"    <issuer>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk0Wt08aPrUmhP+l+hfXfLDzMiRmZKeUYokzrxq7ybfsPN2OxElgcXud0tq8ktpMRzmh7Aua6y4AFU4QZjx9oR9W06rRaBO5fLWTOojjHNKoJ7ujRi3u8BBwNGTdmScGiN0rDsbd6k4b1OCOBJnW9pgcxQHTE5j9TiLoQvPRJd7amp7svTFr1jyfTmCHVbJ0Wmu8GByEAVz9Gl5tozn/Q3Ce0ZJiyPDOZPaK2GBjP9kLxBnxdB36o6zc7IRR39liioU+MoRJLLq/FD6hO3o0b+Mbt1t6LR6aTta0j0UThRby5Ajrhaq9tpQ+qyErlI2nFfCAPQSorauFMtgG4sXmeaQIDAQAB</issuer>\n" + 
				"    <intermediary>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgAPINocbXiiHIOiy1sb4YAJA9HpSvHL4l//R7A0kgKYH9HmBn6HS67Qs3Lzumm5l5mjC4pyPW3b75TqvM6QKKpiF8f0BwbCV46s0e6PDMLicLsT9AB1tjkRYBQ3TT8putUPxcOKxUZs34Aq6LlxIHh+9Ox/FXGHk2HWvsTus2cugCU3XKyaC4wymNyGg2Srp1EQ9HyO+soPr6CxD0SE7eP0zf51MNJ5kaZnuBfy7MVIT+TNr+YYtfkX/f3D9qrFkbBMfyvFchgbDb53opb6kih4SdBFiqcC+KoJTVwaMZhVzi1z6Nlcg7EK8ADsC/u5w5yXFbYkXJVx+vK9mUUS+YQIDAQAB</intermediary>\n" + 
				"    <substitutionAllowed>true</substitutionAllowed>\n" + 
				"    <delegationAllowed>true</delegationAllowed>\n" + 
				"    <validity>\n" + 
				"        <notBefore>2019-04-09T11:37:50.726+03:00</notBefore>\n" + 
				"        <notAfter>2019-04-09T11:54:30.726+03:00</notAfter>\n" + 
				"    </validity>\n" + 
				"    <domain name=\"order\" version=\"1.0\">\n" + 
				"        <Ordering>\n" + 
				"            <ammount>0</ammount>\n" + 
				"        </Ordering>\n" + 
				"    </domain>\n" + 
				"<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"mandator\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>Gs61S5qbACNk0lmQlEjm5tOhkQ8=</DigestValue></Reference></SignedInfo><SignatureValue>Ci8Z40pxOWY4xmuJcv/QEMhCDLKMYWlrIQxUHPTmDy0htF1kyN2sbeUQEiasApxot8So6YlNz83r\n" + 
				"c2CGrsAQjOnIKxeA6g1gSqyCSTByyZGSO0icGYCdnBbldzX7AgRVOt6WZs0eE8JHVqf/+twfEpZH\n" + 
				"eo/Kk5fjhoqyUAxruYaz1os/crOlmH5rZ8H2IVQ0pnBkUTjB9If7KWIAbSwuZJEb+bkOvPmhEBND\n" + 
				"TidUSxbnCe2ObfR7MK+fcVN0RR5NPpBScuWvp8ogcJRu04bFOMRJMAiZXw5jaUpgoTnCdyhDHD1y\n" + 
				"ePVUSL9TQ85N0Uqsv5zTCNjNGfAVKIfEN1fsPw==</SignatureValue><KeyInfo><KeyValue><RSAKeyValue><Modulus>k0Wt08aPrUmhP+l+hfXfLDzMiRmZKeUYokzrxq7ybfsPN2OxElgcXud0tq8ktpMRzmh7Aua6y4AF\n" + 
				"U4QZjx9oR9W06rRaBO5fLWTOojjHNKoJ7ujRi3u8BBwNGTdmScGiN0rDsbd6k4b1OCOBJnW9pgcx\n" + 
				"QHTE5j9TiLoQvPRJd7amp7svTFr1jyfTmCHVbJ0Wmu8GByEAVz9Gl5tozn/Q3Ce0ZJiyPDOZPaK2\n" + 
				"GBjP9kLxBnxdB36o6zc7IRR39liioU+MoRJLLq/FD6hO3o0b+Mbt1t6LR6aTta0j0UThRby5Ajrh\n" + 
				"aq9tpQ+qyErlI2nFfCAPQSorauFMtgG4sXmeaQ==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue></KeyValue></KeyInfo></Signature></delegation>\n" + 
				""; 
		
		String delegation2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
				+ "<delegation xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">\n" + 
				"    <information>\n" + 
				"        <version>1.0</version>\n" + 
				"        <sequence>1</sequence>\n" + 
				"    </information>\n" + 
				"    <issuedDate>2019-04-09T11:37:50.714+03:00</issuedDate>\n" + 
				"    <issuer>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk0Wt08aPrUmhP+l+hfXfLDzMiRmZKeUYokzrxq7ybfsPN2OxElgcXud0tq8ktpMRzmh7Aua6y4AFU4QZjx9oR9W06rRaBO5fLWTOojjHNKoJ7ujRi3u8BBwNGTdmScGiN0rDsbd6k4b1OCOBJnW9pgcxQHTE5j9TiLoQvPRJd7amp7svTFr1jyfTmCHVbJ0Wmu8GByEAVz9Gl5tozn/Q3Ce0ZJiyPDOZPaK2GBjP9kLxBnxdB36o6zc7IRR39liioU+MoRJLLq/FD6hO3o0b+Mbt1t6LR6aTta0j0UThRby5Ajrhaq9tpQ+qyErlI2nFfCAPQSorauFMtgG4sXmeaQIDAQAB</issuer>\n" + 
				"    <intermediary>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgAPINocbXiiHIOiy1sb4YAJA9HpSvHL4l//R7A0kgKYH9HmBn6HS67Qs3Lzumm5l5mjC4pyPW3b75TqvM6QKKpiF8f0BwbCV46s0e6PDMLicLsT9AB1tjkRYBQ3TT8putUPxcOKxUZs34Aq6LlxIHh+9Ox/FXGHk2HWvsTus2cugCU3XKyaC4wymNyGg2Srp1EQ9HyO+soPr6CxD0SE7eP0zf51MNJ5kaZnuBfy7MVIT+TNr+YYtfkX/f3D9qrFkbBMfyvFchgbDb53opb6kih4SdBFiqcC+KoJTVwaMZhVzi1z6Nlcg7EK8ADsC/u5w5yXFbYkXJVx+vK9mUUS+YQIDAQAB</intermediary>\n" + 
				"    <substitutionAllowed>true</substitutionAllowed>\n" + 
				"    <delegationAllowed>true</delegationAllowed>\n" + 
				"    <validity>\n" + 
				"        <notBefore>2019-04-09T11:37:50.726+03:00</notBefore>\n" + 
				"        <notAfter>2019-04-09T11:54:30.726+03:00</notAfter>\n" + 
				"    </validity>\n" + 
				"    <domain name=\"order\" version=\"1.0\">\n" + 
				"        <Ordering>\n" + 
				"            <ammount>0</ammount>\n" + 
				"        </Ordering>\n" + 
				"    </domain>\n" + 
				"<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"mandator\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>Gs61S5qbACNk0lmQlEjm5tOhkQ8=</DigestValue></Reference></SignedInfo><SignatureValue>Ci8Z40pxOWY4xmuJcv/QEMhCDLKMYWlrIQxUHPTmDy0htF1kyN2sbeUQEiasApxot8So6YlNz83r\n" + 
				"c2CGrsAQjOnIKxeA6g1gSqyCSTByyZGSO0icGYCdnBbldzX7AgRVOt6WZs0eE8JHVqf/+twfEpZH\n" + 
				"eo/Kk5fjhoqyUAxruYaz1os/crOlmH5rZ8H2IVQ0pnBkUTjB9If7KWIAbSwuZJEb+bkOvPmhEBND\n" + 
				"TidUSxbnCe2ObfR7MK+fcVN0RR5NPpBScuWvp8ogcJRu04bFOMRJMAiZXw5jaUpgoTnCdyhDHD1y\n" + 
				"ePVUSL9TQ85N0Uqsv5zTCNjNGfAVKIfEN1fsPw==</SignatureValue><KeyInfo><KeyValue><RSAKeyValue><Modulus>k0Wt08aPrUmhP+l+hfXfLDzMiRmZKeUYokzrxq7ybfsPN2OxElgcXud0tq8ktpMRzmh7Aua6y4AF\n" + 
				"U4QZjx9oR9W06rRaBO5fLWTOojjHNKoJ7ujRi3u8BBwNGTdmScGiN0rDsbd6k4b1OCOBJnW9pgcx\n" + 
				"QHTE5j9TiLoQvPRJd7amp7svTFr1jyfTmCHVbJ0Wmu8GByEAVz9Gl5tozn/Q3Ce0ZJiyPDOZPaK2\n" + 
				"GBjP9kLxBnxdB36o6zc7IRR39liioU+MoRJLLq/FD6hO3o0b+Mbt1t6LR6aTta0j0UThRby5Ajrh\n" + 
				"aq9tpQ+qyErlI2nFfCAPQSorauFMtgG4sXmeaQ==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue></KeyValue></KeyInfo></Signature></delegation>\n" + 
				""; 
		
		String delegation3 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
				"<delegation xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\">\n" + 
				"    <information>\n" + 
				"        <version>1.0</version>\n" + 
				"        <sequence>1</sequence>\n" + 
				"    </information>\n" + 
				"    <issuedDate>2019-04-09T11:37:50.714+03:00</issuedDate>\n" + 
				"    <issuer>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk0Wt08aPrUmhP+l+hfXfLDzMiRmZKeUYokzrxq7ybfsPN2OxElgcXud0tq8ktpMRzmh7Aua6y4AFU4QZjx9oR9W06rRaBO5fLWTOojjHNKoJ7ujRi3u8BBwNGTdmScGiN0rDsbd6k4b1OCOBJnW9pgcxQHTE5j9TiLoQvPRJd7amp7svTFr1jyfTmCHVbJ0Wmu8GByEAVz9Gl5tozn/Q3Ce0ZJiyPDOZPaK2GBjP9kLxBnxdB36o6zc7IRR39liioU+MoRJLLq/FD6hO3o0b+Mbt1t6LR6aTta0j0UThRby5Ajrhaq9tpQ+qyErlI2nFfCAPQSorauFMtgG4sXmeaQIDAQAB</issuer>\n" + 
				"    <intermediary>MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgAPINocbXiiHIOiy1sb4YAJA9HpSvHL4l//R7A0kgKYH9HmBn6HS67Qs3Lzumm5l5mjC4pyPW3b75TqvM6QKKpiF8f0BwbCV46s0e6PDMLicLsT9AB1tjkRYBQ3TT8putUPxcOKxUZs34Aq6LlxIHh+9Ox/FXGHk2HWvsTus2cugCU3XKyaC4wymNyGg2Srp1EQ9HyO+soPr6CxD0SE7eP0zf51MNJ5kaZnuBfy7MVIT+TNr+YYtfkX/f3D9qrFkbBMfyvFchgbDb53opb6kih4SdBFiqcC+KoJTVwaMZhVzi1z6Nlcg7EK8ADsC/u5w5yXFbYkXJVx+vK9mUUS+YQIDAQAB</intermediary>\n" + 
				"    <substitutionAllowed>true</substitutionAllowed>\n" + 
				"    <delegationAllowed>true</delegationAllowed>\n" + 
				"    <validity>\n" + 
				"        <notBefore>2019-04-09T11:37:50.726+03:00</notBefore>\n" + 
				"        <notAfter>2019-04-09T11:54:30.726+03:00</notAfter>\n" + 
				"    </validity>\n" + 
				"    <domain name=\"order\" version=\"1.0\">\n" + 
				"        <Ordering>\n" + 
				"            <ammount>0</ammount>\n" + 
				"        </Ordering>\n" + 
				"    </domain>\n" + 
				"<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"mandator\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>Gs61S5qbACNk0lmQlEjm5tOhkQ8=</DigestValue></Reference></SignedInfo><SignatureValue>Ci8Z40pxOWY4xmuJcv/QEMhCDLKMYWlrIQxUHPTmDy0htF1kyN2sbeUQEiasApxot8So6YlNz83r\n" + 
				"c2CGrsAQjOnIKxeA6g1gSqyCSTByyZGSO0icGYCdnBbldzX7AgRVOt6WZs0eE8JHVqf/+twfEpZH\n" + 
				"eo/Kk5fjhoqyUAxruYaz1os/crOlmH5rZ8H2IVQ0pnBkUTjB9If7KWIAbSwuZJEb+bkOvPmhEBND\n" + 
				"TidUSxbnCe2ObfR7MK+fcVN0RR5NPpBScuWvp8ogcJRu04bFOMRJMAiZXw5jaUpgoTnCdyhDHD1y\n" + 
				"ePVUSL9TQ85N0Uqsv5zTCNjNGfAVKIfEN1fsPw==</SignatureValue><KeyInfo><KeyValue><RSAKeyValue><Modulus>k0Wt08aPrUmhP+l+hfXfLDzMiRmZKeUYokzrxq7ybfsPN2OxElgcXud0tq8ktpMRzmh7Aua6y4AF\n" + 
				"U4QZjx9oR9W06rRaBO5fLWTOojjHNKoJ7ujRi3u8BBwNGTdmScGiN0rDsbd6k4b1OCOBJnW9pgcx\n" + 
				"QHTE5j9TiLoQvPRJd7amp7svTFr1jyfTmCHVbJ0Wmu8GByEAVz9Gl5tozn/Q3Ce0ZJiyPDOZPaK2\n" + 
				"GBjP9kLxBnxdB36o6zc7IRR39liioU+MoRJLLq/FD6hO3o0b+Mbt1t6LR6aTta0j0UThRby5Ajrh\n" + 
				"aq9tpQ+qyErlI2nFfCAPQSorauFMtgG4sXmeaQ==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue></KeyValue></KeyInfo></Signature></delegation>\n" + 
				""; 
		
		assertTrue(DelegationApi.verifyDelegationXml(delegation1, mandatorPublicKey, false));
		assertTrue(DelegationApi.verifyDelegationXml(delegation2, mandatorPublicKey, false));
		assertTrue(DelegationApi.verifyDelegationXml(delegation3, mandatorPublicKey, false));
	}
	
	public static PublicKey getKey(String key){
	    try{
	        byte[] byteKey = IOUtil.decodeBase64(key);
	        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
	        KeyFactory kf = KeyFactory.getInstance("RSA");

	        return kf.generatePublic(X509publicKey);
	    }
	    catch(Exception e){
	    	logger.error("getKey exception: " + e.getMessage(), e);
	    }

	    return null;
	}
}
