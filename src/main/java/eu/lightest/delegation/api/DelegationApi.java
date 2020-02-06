package eu.lightest.delegation.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Collections;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.security.Key;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.XMLConstants;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.lightest.delegation.api.util.IOUtil;

import static javax.crypto.Cipher.ENCRYPT_MODE;

@SuppressWarnings("restriction")
public class DelegationApi {
	
	//AES supports key lengths of 128, 192 and 256 bit
	private static final int SYMMETRIC_KEY_DEFAULT_LENGTH = 16;//256 / 8;
	
	private static final String DEFAULT_INIT_VECTOR = "0123456789012345";
	
	private static final String DIGEST_METHOD = DigestMethod.SHA1;
	
	private static final String SIGNATURE_METHOD = SignatureMethod.RSA_SHA1;
	
	private static final String MANDATOR_SIGNATURE_NODE_ID = "mandator";
	
	private static final String INTERMEDIARY_SIGNATURE_NODE_ID = "intermediary";
	
	private static KeyPairGenerator generator;
	
	private DelegationApi() {
	}
	
	public static String signDelegationXml(String delegationXml, PrivateKey privateKey, PublicKey publicKey, boolean isIntermediary) throws ParserConfigurationException, SAXException, IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, KeyException, MarshalException, XMLSignatureException, TransformerException {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(new ByteArrayInputStream(delegationXml.getBytes()));
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        document.getDocumentElement().normalize();
        
        // Create signing context
        DOMSignContext dsc = new DOMSignContext(privateKey, document.getDocumentElement());

        // Assembling the XML structure
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        Reference ref = fac.newReference
                ("", fac.newDigestMethod(DIGEST_METHOD, null),
                        Collections.singletonList
                                (fac.newTransform(Transform.ENVELOPED,
                                        (TransformParameterSpec) null)), null, null);

        SignedInfo si = fac.newSignedInfo
                (fac.newCanonicalizationMethod
                                (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                        (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SIGNATURE_METHOD, null),
                        Collections.singletonList(ref));

        KeyInfoFactory kif = fac.getKeyInfoFactory();
        KeyValue kv = kif.newKeyValue(publicKey);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

        XMLSignature signature = null;
        if(isIntermediary)
            signature = fac.newXMLSignature(si, ki, null, INTERMEDIARY_SIGNATURE_NODE_ID, null);
        else
            signature = fac.newXMLSignature(si, ki, null, MANDATOR_SIGNATURE_NODE_ID, null);

        signature.sign(dsc);
        
        // Return the signed Document
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        
        
        Transformer t = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(bos);
        t.transform(source, result);
		
		return bos.toString();
	}
	
	public static boolean verifyDelegationXml(String delegationXml, PublicKey publicKey, boolean isIntermediary) throws ParserConfigurationException, SAXException, IOException, DelegationAPIException, MarshalException {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(new ByteArrayInputStream(delegationXml.getBytes()));
        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        document.getDocumentElement().normalize();
        
        NodeList signatureNodeList = document.getElementsByTagName("Signature");

	    if (signatureNodeList.getLength() == 0) {
	        throw new DelegationAPIException("Any signature element not found in given delegation xml file");
	    }
	    
	    Node signatureNode = null;
	    for(int i=0 ; i<signatureNodeList.getLength() ; i++) {
	    	Node node = signatureNodeList.item(i);
	    	if(!node.hasAttributes() || node.getAttributes().getNamedItem("Id") == null)
	    		continue;
	    		
	    	String idAttribute = node.getAttributes().getNamedItem("Id").getNodeValue();
	    	
	    	if((isIntermediary && INTERMEDIARY_SIGNATURE_NODE_ID.equals(idAttribute)) || 
	    			(!isIntermediary && MANDATOR_SIGNATURE_NODE_ID.equals(idAttribute))) {
	    		signatureNode = node;
	    		break;
	    	}
	    }
	    
	    if (signatureNode == null) {
	        throw new DelegationAPIException("Related signature element not found in given delegation xml file");
	    }

	    DOMValidateContext dvc = new DOMValidateContext(publicKey, signatureNode);
	    
	    // Assembling the XML structure
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
	    XMLSignature signature = fac.unmarshalXMLSignature(dvc);

	    try {
	        return signature.validate(dvc);
	    }catch(final XMLSignatureException e) {
	        throw new DelegationAPIException("Signature validation is failed", e);
	    }
	}
	
	public static byte[] createSymmetricKey() {
		byte keyValue[] =  IOUtil.randomByteArray(SYMMETRIC_KEY_DEFAULT_LENGTH);
		Key k = new SecretKeySpec(keyValue, "AES");
		return k.getEncoded();
	}
	
	public static String encryptSymmetricKey(byte[] symmetricKey, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher encryptCipher = Cipher.getInstance(publicKey.getAlgorithm());
        encryptCipher.init(ENCRYPT_MODE, publicKey);

        byte[] cipherText = encryptCipher.doFinal(symmetricKey);

        return IOUtil.encodeBase64(cipherText);
	}
	
	public static byte[] decryptSymmetricKey(String encryptedSymmetricKey, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = IOUtil.decodeBase64(encryptedSymmetricKey);

        Cipher decriptCipher = Cipher.getInstance(privateKey.getAlgorithm());
        decriptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        return decriptCipher.doFinal(bytes);
	}
	
	public static String encryptDelegationXml(String data, byte[] symmetricKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		IvParameterSpec iv = new IvParameterSpec(DEFAULT_INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
		SecretKeySpec skeySpec = new SecretKeySpec(symmetricKey, "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

		byte[] encrypted = cipher.doFinal(data.getBytes());

        return IOUtil.encodeBase64(encrypted);
	}
	
	public static String decryptDelegationXml(String data, byte[] symmetricKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
		IvParameterSpec iv = new IvParameterSpec(DEFAULT_INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
		SecretKeySpec skeySpec = new SecretKeySpec(symmetricKey, "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		byte[] decrypted = cipher.doFinal(Base64.decodeBase64(data));

		return new String(decrypted);
	}
	
	public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		if(generator == null) {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024, new SecureRandom());
		}
		
		return generator.generateKeyPair();
	}
	
}
