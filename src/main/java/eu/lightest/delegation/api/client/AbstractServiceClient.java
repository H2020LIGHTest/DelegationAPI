package eu.lightest.delegation.api.client;

public abstract class AbstractServiceClient {
	
	//private static String dpRestServiceBaseUrl = "http://localhost:8080/DelegationProvider/api/v1";
    private static String dpRestServiceBaseUrl = "https://dp.tug.do.nlnetlabs.nl/dp/api/v1";

	protected AbstractServiceClient() {
	}

	public static String getDpRestServiceBaseUrl() {
		return dpRestServiceBaseUrl;
	}

	public static void setDpRestServiceBaseUrl(String dpRestServiceBaseUrl) {
		AbstractServiceClient.dpRestServiceBaseUrl = dpRestServiceBaseUrl;
	}

}
