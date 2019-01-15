
package com.example.jon.politiswap.DataUtils.Recent;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class CosponsorsByParty implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.d);
        dest.writeValue(this.r);
    }

    public CosponsorsByParty() {
    }

    protected CosponsorsByParty(Parcel in) {
        this.d = (Integer) in.readValue(Integer.class.getClassLoader());
        this.r = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<CosponsorsByParty> CREATOR = new Parcelable.Creator<CosponsorsByParty>() {
        @Override
        public CosponsorsByParty createFromParcel(Parcel source) {
            return new CosponsorsByParty(source);
        }

        @Override
        public CosponsorsByParty[] newArray(int size) {
            return new CosponsorsByParty[size];
        }
    };
}
