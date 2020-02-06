package eu.lightest.delegation.api.client.model;

import com.google.gson.annotations.SerializedName;

public class JsonDownloadDelegation {

    @SerializedName("id")
    private String mId;

    @SerializedName("data")
    private String mData;

    @SerializedName("key")
    private String mKey;

    public JsonDownloadDelegation(int id, String data, String key) {
        mId = String.valueOf(id);
        mData = data;
        mKey = key;
    }

    public String getId() {
        return mId;
    }

    public String getData() {
        return mData;
    }
    
    public String getKey() {
        return mKey;
    }
}
