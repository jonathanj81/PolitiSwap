
package com.politiswap.politiswap.DataUtils.Recent;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Keep;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Bill implements Parcelable {

    @SerializedName("bill_id")
    @Expose
    private String billId;
    @SerializedName("bill_slug")
    @Expose
    private String billSlug;
    @SerializedName("bill_type")
    @Expose
    private String billType;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("bill_uri")
    @Expose
    private String billUri;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("short_title")
    @Expose
    private String shortTitle;
    @SerializedName("sponsor_title")
    @Expose
    private String sponsorTitle;
    @SerializedName("sponsor_id")
    @Expose
    private String sponsorId;
    @SerializedName("sponsor_name")
    @Expose
    private String sponsorName;
    @SerializedName("sponsor_state")
    @Expose
    private String sponsorState;
    @SerializedName("sponsor_party")
    @Expose
    private String sponsorParty;
    @SerializedName("sponsor_uri")
    @Expose
    private String sponsorUri;
    @SerializedName("gpo_pdf_uri")
    @Expose
    private String gpoPdfUri;
    @SerializedName("congressdotgov_url")
    @Expose
    private String congressdotgovUrl;
    @SerializedName("govtrack_url")
    @Expose
    private String govtrackUrl;
    @SerializedName("introduced_date")
    @Expose
    private String introducedDate;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("last_vote")
    @Expose
    private String lastVote;
    @SerializedName("house_passage")
    @Expose
    private String housePassage;
    @SerializedName("senate_passage")
    @Expose
    private String senatePassage;
    @SerializedName("enacted")
    @Expose
    private String enacted;
    @SerializedName("vetoed")
    @Expose
    private String vetoed;
    @SerializedName("cosponsors")
    @Expose
    private Integer cosponsors;
    @SerializedName("cosponsors_by_party")
    @Expose
    private CosponsorsByParty cosponsorsByParty;
    @SerializedName("committees")
    @Expose
    private String committees;
    @SerializedName("committee_codes")
    @Expose
    private List<String> committeeCodes = null;
    @SerializedName("subcommittee_codes")
    @Expose
    private List<String> subcommitteeCodes = null;
    @SerializedName("primary_subject")
    @Expose
    private String primarySubject;
    @SerializedName("summary")
    @Expose
    private String summary;
    @SerializedName("summary_short")
    @Expose
    private String summaryShort;
    @SerializedName("latest_major_action_date")
    @Expose
    private String latestMajorActionDate;
    @SerializedName("latest_major_action")
    @Expose
    private String latestMajorAction;

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getBillSlug() {
        return billSlug;
    }

    public void setBillSlug(String billSlug) {
        this.billSlug = billSlug;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBillUri() {
        return billUri;
    }

    public void setBillUri(String billUri) {
        this.billUri = billUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getSponsorTitle() {
        return sponsorTitle;
    }

    public void setSponsorTitle(String sponsorTitle) {
        this.sponsorTitle = sponsorTitle;
    }

    public String getSponsorId() {
        return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
        this.sponsorId = sponsorId;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getSponsorState() {
        return sponsorState;
    }

    public void setSponsorState(String sponsorState) {
        this.sponsorState = sponsorState;
    }

    public String getSponsorParty() {
        return sponsorParty;
    }

    public void setSponsorParty(String sponsorParty) {
        this.sponsorParty = sponsorParty;
    }

    public String getSponsorUri() {
        return sponsorUri;
    }

    public void setSponsorUri(String sponsorUri) {
        this.sponsorUri = sponsorUri;
    }

    public Object getGpoPdfUri() {
        return gpoPdfUri;
    }

    public void setGpoPdfUri(String gpoPdfUri) {
        this.gpoPdfUri = gpoPdfUri;
    }

    public String getCongressdotgovUrl() {
        return congressdotgovUrl;
    }

    public void setCongressdotgovUrl(String congressdotgovUrl) {
        this.congressdotgovUrl = congressdotgovUrl;
    }

    public String getGovtrackUrl() {
        return govtrackUrl;
    }

    public void setGovtrackUrl(String govtrackUrl) {
        this.govtrackUrl = govtrackUrl;
    }

    public String getIntroducedDate() {
        return introducedDate;
    }

    public void setIntroducedDate(String introducedDate) {
        this.introducedDate = introducedDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Object getLastVote() {
        return lastVote;
    }

    public void setLastVote(String lastVote) {
        this.lastVote = lastVote;
    }

    public Object getHousePassage() {
        return housePassage;
    }

    public void setHousePassage(String housePassage) {
        this.housePassage = housePassage;
    }

    public Object getSenatePassage() {
        return senatePassage;
    }

    public void setSenatePassage(String senatePassage) {
        this.senatePassage = senatePassage;
    }

    public Object getEnacted() {
        return enacted;
    }

    public void setEnacted(String enacted) {
        this.enacted = enacted;
    }

    public Object getVetoed() {
        return vetoed;
    }

    public void setVetoed(String vetoed) {
        this.vetoed = vetoed;
    }

    public Integer getCosponsors() {
        return cosponsors;
    }

    public void setCosponsors(Integer cosponsors) {
        this.cosponsors = cosponsors;
    }

    public CosponsorsByParty getCosponsorsByParty() {
        return cosponsorsByParty;
    }

    public void setCosponsorsByParty(CosponsorsByParty cosponsorsByParty) {
        this.cosponsorsByParty = cosponsorsByParty;
    }

    public String getCommittees() {
        return committees;
    }

    public void setCommittees(String committees) {
        this.committees = committees;
    }

    public List<String> getCommitteeCodes() {
        return committeeCodes;
    }

    public void setCommitteeCodes(List<String> committeeCodes) {
        this.committeeCodes = committeeCodes;
    }

    public List<String> getSubcommitteeCodes() {
        return subcommitteeCodes;
    }

    public void setSubcommitteeCodes(List<String> subcommitteeCodes) {
        this.subcommitteeCodes = subcommitteeCodes;
    }

    public String getPrimarySubject() {
        return primarySubject;
    }

    public void setPrimarySubject(String primarySubject) {
        this.primarySubject = primarySubject;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummaryShort() {
        return summaryShort;
    }

    public void setSummaryShort(String summaryShort) {
        this.summaryShort = summaryShort;
    }

    public String getLatestMajorActionDate() {
        return latestMajorActionDate;
    }

    public void setLatestMajorActionDate(String latestMajorActionDate) {
        this.latestMajorActionDate = latestMajorActionDate;
    }

    public String getLatestMajorAction() {
        return latestMajorAction;
    }

    public void setLatestMajorAction(String latestMajorAction) {
        this.latestMajorAction = latestMajorAction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.billId);
        dest.writeString(this.billSlug);
        dest.writeString(this.billType);
        dest.writeString(this.number);
        dest.writeString(this.billUri);
        dest.writeString(this.title);
        dest.writeString(this.shortTitle);
        dest.writeString(this.sponsorTitle);
        dest.writeString(this.sponsorId);
        dest.writeString(this.sponsorName);
        dest.writeString(this.sponsorState);
        dest.writeString(this.sponsorParty);
        dest.writeString(this.sponsorUri);
        dest.writeString(this.gpoPdfUri);
        dest.writeString(this.congressdotgovUrl);
        dest.writeString(this.govtrackUrl);
        dest.writeString(this.introducedDate);
        dest.writeValue(this.active);
        dest.writeString(this.lastVote);
        dest.writeString(this.housePassage);
        dest.writeString(this.senatePassage);
        dest.writeString(this.enacted);
        dest.writeString(this.vetoed);
        dest.writeValue(this.cosponsors);
        dest.writeParcelable(this.cosponsorsByParty, flags);
        dest.writeString(this.committees);
        dest.writeStringList(this.committeeCodes);
        dest.writeStringList(this.subcommitteeCodes);
        dest.writeString(this.primarySubject);
        dest.writeString(this.summary);
        dest.writeString(this.summaryShort);
        dest.writeString(this.latestMajorActionDate);
        dest.writeString(this.latestMajorAction);
    }

    public Bill() {
    }

    protected Bill(Parcel in) {
        this.billId = in.readString();
        this.billSlug = in.readString();
        this.billType = in.readString();
        this.number = in.readString();
        this.billUri = in.readString();
        this.title = in.readString();
        this.shortTitle = in.readString();
        this.sponsorTitle = in.readString();
        this.sponsorId = in.readString();
        this.sponsorName = in.readString();
        this.sponsorState = in.readString();
        this.sponsorParty = in.readString();
        this.sponsorUri = in.readString();
        this.gpoPdfUri = in.readString();
        this.congressdotgovUrl = in.readString();
        this.govtrackUrl = in.readString();
        this.introducedDate = in.readString();
        this.active = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.lastVote = in.readString();
        this.housePassage = in.readString();
        this.senatePassage = in.readString();
        this.enacted = in.readString();
        this.vetoed = in.readString();
        this.cosponsors = (Integer) in.readValue(Integer.class.getClassLoader());
        this.cosponsorsByParty = in.readParcelable(CosponsorsByParty.class.getClassLoader());
        this.committees = in.readString();
        this.committeeCodes = in.createStringArrayList();
        this.subcommitteeCodes = in.createStringArrayList();
        this.primarySubject = in.readString();
        this.summary = in.readString();
        this.summaryShort = in.readString();
        this.latestMajorActionDate = in.readString();
        this.latestMajorAction = in.readString();
    }

    public static final Parcelable.Creator<Bill> CREATOR = new Parcelable.Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel source) {
            return new Bill(source);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };
}
