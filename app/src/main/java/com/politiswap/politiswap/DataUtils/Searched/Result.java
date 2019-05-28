
package com.politiswap.politiswap.DataUtils.Searched;

import androidx.annotation.Keep;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Result {

    @SerializedName("num_results")
    @Expose
    private Integer numResults;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("bills")
    @Expose
    private List<Bill> bills = null;

    public Integer getNumResults() {
        return numResults;
    }

    public void setNumResults(Integer numResults) {
        this.numResults = numResults;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

}
