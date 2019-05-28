package com.politiswap.politiswap.DataUtils;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@Keep
public class Policy implements Parcelable {

    private String creator;
    private int netWanted;
    private String party;
    private List<String> subjects;
    private String title;
    private String summary;
    private String timeStamp;
    private String longID;

    public Policy(){

    }

    public Policy(String creator, int netWanted, String party, List<String> subjects, String title, String summary, String timeStamp, String longID){
        this.timeStamp = timeStamp;
        this.summary = summary;
        this.title = title;
        this.subjects = subjects;
        this.party = party;
        this.netWanted = netWanted;
        this.creator = creator;
        this.longID = longID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getSummary() {
        return summary;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<String> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
    public String getParty() {
        return party;
    }
    public void setParty(String party) {
        this.party = party;
    }
    public int getNetWanted() {
        return netWanted;
    }
    public void setNetWanted(int netWanted) {
        this.netWanted = netWanted;
    }
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getLongID() {
        return longID;
    }
    public void setLongID(String longID) {
        this.longID = longID;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Policy))
            return false;
        if (obj == this)
            return true;
        return this.getLongID().equals(((Policy) obj).getLongID());
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(19,29)
                .append(longID)
                .toHashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.creator);
        dest.writeInt(this.netWanted);
        dest.writeString(this.party);
        dest.writeStringList(this.subjects);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.timeStamp);
        dest.writeString(this.longID);
    }

    protected Policy(Parcel in) {
        this.creator = in.readString();
        this.netWanted = in.readInt();
        this.party = in.readString();
        this.subjects = in.createStringArrayList();
        this.title = in.readString();
        this.summary = in.readString();
        this.timeStamp = in.readString();
        this.longID = in.readString();
    }

    public static final Parcelable.Creator<Policy> CREATOR = new Parcelable.Creator<Policy>() {
        @Override
        public Policy createFromParcel(Parcel source) {
            return new Policy(source);
        }

        @Override
        public Policy[] newArray(int size) {
            return new Policy[size];
        }
    };
}
