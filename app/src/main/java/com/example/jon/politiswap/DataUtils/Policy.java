package com.example.jon.politiswap.DataUtils;

import java.util.List;

public class Policy {

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
}
