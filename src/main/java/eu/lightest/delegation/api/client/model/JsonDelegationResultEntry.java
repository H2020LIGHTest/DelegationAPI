package eu.lightest.delegation.api.client.model;

import com.google.gson.annotations.SerializedName;

public class JsonDelegationResultEntry {

	@SerializedName("id")
    private String mId;

    @SerializedName("status")
    private String mStatus;

    @SerializedName("data")
    private String mData;

    public String toString() {
        return "{\n"
                + "\tid: " + mId + ",\n"
                + "\tstatus: " + mStatus + ",\n"
                + "\tdata: " + mData + "\n"
                + "}";
    }

    public void setId(String id) {
        mId = id;

    }

    public String getId() {
        return mId;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setData(String data) {
        mData = data;
    }

    public String getData() {
        return mData;
    }
}
