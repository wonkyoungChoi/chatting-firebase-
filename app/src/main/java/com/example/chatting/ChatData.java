package com.example.chatting;

import java.io.Serializable;

public class ChatData{
    private String massage;
    private String nickname;
    private String profilePic;

    public ChatData(String nickname, String massage, String profilePic) {
        this.nickname = nickname;
        this.massage = massage;
        this.profilePic = profilePic;
    }

    public ChatData() { }

    public String getMassage() { return massage; }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfilePic() { return profilePic; }

    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
}
