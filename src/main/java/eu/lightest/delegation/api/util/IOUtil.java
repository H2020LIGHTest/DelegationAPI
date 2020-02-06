package eu.lightest.delegation.api.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Base64;

public class IOUtil {
	
	private IOUtil() {
	}
		
	public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    public static byte[] decompressGziptoByteArray(InputStream is) throws IOException {
        GZIPInputStream gzis = new GZIPInputStream(is);
        return toByteArray(gzis);
    }

	public static byte[] randomByteArray(int length) {
		SecureRandom secureRandom = new SecureRandom();
		
		byte[] rand = new byte[length];
		secureRandom.nextBytes(rand);
		
	    return rand;
	}
	
	public static byte[] decodeBase64(String base64Data) {
		return Base64.decodeBase64(base64Data);
	}
	
	public static String encodeBase64(byte[] data) {
		return Base64.encodeBase64String(data);
	}

	public static String encodeBase32(byte[] data) {
		Base32 base32 = new Base32();
		return base32.encodeAsString(data);
	}
	
	public static byte[] calculateHash(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return digest.digest(data);
	}
	
	public static String bytesToHex(byte[] hash) {
	    StringBuilder hexString = new StringBuilder();
	    for (int i = 0; i < hash.length; i++) {
		    String hex = Integer.toHexString(0xff & hash[i]);
		    if(hex.length() == 1) 
		    	hexString.append('0');
		        
		    hexString.append(hex);
	    }
	    return hexString.toString();
	}

}
