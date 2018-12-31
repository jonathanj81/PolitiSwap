package com.example.jon.politiswap.DataUtils;

public class Swap {

    private String creator;
    private String timestamp;
    private String demPolicyID;
    private String repPolicyID;
    private String partyOnTop;
    private String longID;
    private double rating;
    private int demNetVotes;
    private int repNetVotes;
    private double policyAvgNet;

    public Swap(){}

    public Swap(String creator, String timestamp, String demPolicyID, String repPolicyID, String partyOnTop,
                String longID, double rating, int demNetVotes, int repNetVotes, double policyAvgNet){
        this.creator = creator;
        this.timestamp = timestamp;
        this.demPolicyID = demPolicyID;
        this.repPolicyID = repPolicyID;
        this.partyOnTop = partyOnTop;
        this.longID = longID;
        this.rating = rating;
        this.demNetVotes = demNetVotes;
        this.repNetVotes = repNetVotes;
        this.policyAvgNet = policyAvgNet;
    }

    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getDemPolicyID() {
        return demPolicyID;
    }
    public void setDemPolicyID(String demPolicyID) {
        this.demPolicyID = demPolicyID;
    }
    public String getRepPolicyID() {
        return repPolicyID;
    }
    public void setRepPolicyID(String repPolicyID) {
        this.repPolicyID = repPolicyID;
    }
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getDemNetVotes() {
        return demNetVotes;
    }

    public void setDemNetVotes(int demNetVotes) {
        this.demNetVotes = demNetVotes;
    }

    public int getRepNetVotes() {
        return repNetVotes;
    }

    public void setRepNetVotes(int repNetVotes) {
        this.repNetVotes = repNetVotes;
    }

    public double getPolicyAvgNet() {
        return policyAvgNet;
    }

    public void setPolicyAvgNet(double policyAvgNet) {
        this.policyAvgNet = policyAvgNet;
    }

    public String getPartyOnTop() {
        return partyOnTop;
    }

    public void setPartyOnTop(String partyOnTop) {
        this.partyOnTop = partyOnTop;
    }

    public String getLongID() {
        return longID;
    }

    public void setLongID(String longID) {
        this.longID = longID;
    }
}
