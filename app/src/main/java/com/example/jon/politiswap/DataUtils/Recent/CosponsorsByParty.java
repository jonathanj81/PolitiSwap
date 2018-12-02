
package com.example.jon.politiswap.DataUtils.Recent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CosponsorsByParty {

    @SerializedName("D")
    @Expose
    private Integer d;
    @SerializedName("R")
    @Expose
    private Integer r;

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
    }

    public Integer getR() {
        return r;
    }

    public void setR(Integer r) {
        this.r = r;
    }

}
