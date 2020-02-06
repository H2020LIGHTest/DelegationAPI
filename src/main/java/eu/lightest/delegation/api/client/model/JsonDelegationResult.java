package eu.lightest.delegation.api.client.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class JsonDelegationResult {

    @SerializedName("result")
    private List<JsonDelegationResultEntry> mResults = new ArrayList<JsonDelegationResultEntry>();

    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{\n");
        for( JsonDelegationResultEntry result : mResults ) {
            builder.append( result );
        }
        builder.append("}");

        return builder.toString();
    }

    public void add(JsonDelegationResultEntry entry) {
        mResults.add(entry);
    }

    public List<JsonDelegationResultEntry> get() {
        return mResults;
    }
}
