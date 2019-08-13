package com.politiswap.politiswap.NewVersion;

import androidx.annotation.Keep;

import java.util.List;

@Keep
public class UserInfo {

    private String username;
    private String party;
    private double overallPoints;
    private double swapCreatedPoints;
    private double policyCreatedPoints;
    private double votePoints;
    private List<String> policiesCreated;
    private List<String> policiesCommented;
    private List<String> policiesVoted;
    private List<String> swapsCreated;
    private List<String> swapsCommented;
    private List<String> swapsVoted;


    public UserInfo(){}

    public UserInfo(String username, String party, double overallPoints, double swapCreatedPoints,
                    double policyCreatedPoints, double votePoints) {
        this.username = username;
        this.party = party;
        this.overallPoints = overallPoints;
        this.swapCreatedPoints = swapCreatedPoints;
        this.policyCreatedPoints = policyCreatedPoints;
        this.votePoints = votePoints;
    }

    public UserInfo(String username, String party, double overallPoints, double swapCreatedPoints,
                    double policyCreatedPoints, double votePoints, List<String> policiesCreated,
                    List<String> policiesCommented, List<String> policiesVoted, List<String> swapsCreated,
                    List<String> swapsCommented, List<String> swapsVoted) {
        this.username = username;
        this.party = party;
        this.overallPoints = overallPoints;
        this.swapCreatedPoints = swapCreatedPoints;
        this.policyCreatedPoints = policyCreatedPoints;
        this.votePoints = votePoints;
        this.policiesCreated = policiesCreated;
        this.policiesCommented = policiesCommented;
        this.policiesVoted = policiesVoted;
        this.swapsCreated = swapsCreated;
        this.swapsCommented = swapsCommented;
        this.swapsVoted = swapsVoted;
    }

    public String getParty() {
        return party;
    }
    public void setParty(String party) {
        this.party = party;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public double getOverallPoints() {
        return overallPoints;
    }

    public void setOverallPoints(double overallPoints) {
        this.overallPoints = overallPoints;
    }

    public double getSwapCreatedPoints() {
        return swapCreatedPoints;
    }

    public void setSwapCreatedPoints(double swapCreatedPoints) {
        this.swapCreatedPoints = swapCreatedPoints;
    }

    public double getPolicyCreatedPoints() {
        return policyCreatedPoints;
    }

    public void setPolicyCreatedPoints(double policyCreatedPoints) {
        this.policyCreatedPoints = policyCreatedPoints;
    }

    public double getVotePoints() {
        return votePoints;
    }

    public void setVotePoints(double votePoints) {
        this.votePoints = votePoints;
    }

    public List<String> getPoliciesCreated() {
        return policiesCreated;
    }

    public void setPoliciesCreated(List<String> policiesCreated) {
        this.policiesCreated = policiesCreated;
    }

    public List<String> getPoliciesCommented() {
        return policiesCommented;
    }

    public void setPoliciesCommented(List<String> policiesCommented) {
        this.policiesCommented = policiesCommented;
    }

    public List<String> getPoliciesVoted() {
        return policiesVoted;
    }

    public void setPoliciesVoted(List<String> policiesVoted) {
        this.policiesVoted = policiesVoted;
    }

    public List<String> getSwapsCreated() {
        return swapsCreated;
    }

    public void setSwapsCreated(List<String> swapsCreated) {
        this.swapsCreated = swapsCreated;
    }

    public List<String> getSwapsCommented() {
        return swapsCommented;
    }

    public void setSwapsCommented(List<String> swapsCommented) {
        this.swapsCommented = swapsCommented;
    }

    public List<String> getSwapsVoted() {
        return swapsVoted;
    }

    public void setSwapsVoted(List<String> swapsVoted) {
        this.swapsVoted = swapsVoted;
    }
}
