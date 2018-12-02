
package com.example.jon.politiswap.DataUtils.Searched;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bill {

    @SerializedName("bill_id")
    @Expose
    private String billId;
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
    @SerializedName("house_passage")
    @Expose
    private Object housePassage;
    @SerializedName("senate_passage")
    @Expose
    private Object senatePassage;
    @SerializedName("enacted")
    @Expose
    private Object enacted;
    @SerializedName("vetoed")
    @Expose
    private Object vetoed;
    @SerializedName("cosponsors")
    @Expose
    private Integer cosponsors;
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

    public String getGpoPdfUri() {
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

    public Object getHousePassage() {
        return housePassage;
    }

    public void setHousePassage(Object housePassage) {
        this.housePassage = housePassage;
    }

    public Object getSenatePassage() {
        return senatePassage;
    }

    public void setSenatePassage(Object senatePassage) {
        this.senatePassage = senatePassage;
    }

    public Object getEnacted() {
        return enacted;
    }

    public void setEnacted(Object enacted) {
        this.enacted = enacted;
    }

    public Object getVetoed() {
        return vetoed;
    }

    public void setVetoed(Object vetoed) {
        this.vetoed = vetoed;
    }

    public Integer getCosponsors() {
        return cosponsors;
    }

    public void setCosponsors(Integer cosponsors) {
        this.cosponsors = cosponsors;
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

}
