package eu.lightest.delegation.api.client.model;

import com.google.gson.annotations.SerializedName;

public class DelegationDataSet {

	@SerializedName("id")
    private int mId = 0;

    @SerializedName("hash")
    private String mHash = null;

    @SerializedName("key")
    private String mKey = null;

    @SerializedName("status")
    private String mStatus = null;

    @SerializedName("data")
    private String mData = null;

    public DelegationDataSet(int id, String hash, String key, String data) {
        this(id, hash, key, data, "UNKNOWN");
    }

    public DelegationDataSet(int id, String hash, String key, String data, String status) {
    	mId = id;
        mHash = hash;
        mKey = key;
        mData = data;
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }

    public int getId() {
        return mId;
    }

    public String getHash() {
        return mHash;
    }

    public String getKey() {
        return mKey;
    }

    public String getData() {
        return mData;
    }
}
