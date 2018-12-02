package com.example.jon.politiswap.DataUtils;

public class UserInfo {

    private String username;
    private String party;

    public UserInfo(){}

    public UserInfo(String username, String party) {
        this.username = username;
        this.party = party;
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
}
