
package com.example.jon.politiswap.DataUtils.Recent;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("congress")
    @Expose
    private Integer congress;
    @SerializedName("chamber")
    @Expose
    private String chamber;
    @SerializedName("num_results")
    @Expose
    private Integer numResults;
    @SerializedName("offset")
    @Expose
    private Integer offset;
    @SerializedName("bills")
    @Expose
    private List<Bill> bills = null;

    public Integer getCongress() {
        return congress;
    }

    public void setCongress(Integer congress) {
        this.congress = congress;
    }

    public String getChamber() {
        return chamber;
    }

    public void setChamber(String chamber) {
        this.chamber = chamber;
    }

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
