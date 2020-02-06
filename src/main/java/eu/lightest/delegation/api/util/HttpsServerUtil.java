package eu.lightest.delegation.api.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 *
 */
public class HttpsServerUtil {
	
	private static boolean configured = false;
	
	private HttpsServerUtil() {
	}

    public static synchronized void configureSSL() throws NoSuchAlgorithmException, KeyManagementException {
    	
    	if(configured)
    		return;

        java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        
        SSLContext ssl =  SSLContext.getInstance("TLSv1.2");
        ssl.init(null, new MyTrustManager[]{new MyTrustManager()}, new SecureRandom());
        
        // set default ssl context
        SSLContext.setDefault(ssl);
        // set default SSLSocketFactory and HostnameVerifier of HttpsURLConnection
        HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());

		//done to prevent CN verification in client keystore
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

           public boolean verify(String hostname, SSLSession session) {
                return true;
           }
        });   
        
        configured = true;
    }

}

class MyTrustManager implements X509TrustManager {

    public MyTrustManager() {
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        //trust all
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        //trust all
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
