package com.politiswap.politiswap.DataUtils;

import android.support.annotation.Keep;

@Keep
public class UserInfo {

    private String username;
    private String party;
    private double overallPoints;
    private double swapCreatedPoints;
    private double policyCreatedPoints;
    private double votePoints;

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
}
