
package com.example.jon.politiswap.DataUtils.Searched;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchedBills implements Parcelable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("copyright")
    @Expose
    private String copyright;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.copyright);
        dest.writeList(this.results);
    }

    public SearchedBills() {
    }

    protected SearchedBills(Parcel in) {
        this.status = in.readString();
        this.copyright = in.readString();
        this.results = new ArrayList<Result>();
        in.readList(this.results, Result.class.getClassLoader());
    }

    public static final Parcelable.Creator<SearchedBills> CREATOR = new Parcelable.Creator<SearchedBills>() {
        @Override
        public SearchedBills createFromParcel(Parcel source) {
            return new SearchedBills(source);
        }

        @Override
        public SearchedBills[] newArray(int size) {
            return new SearchedBills[size];
        }
    };
}
