package com.example.chatting;

import java.io.Serializable;

public class ChatData{
    private String message;
    private String nickname;
    private String profilePic;
    private String key;

    public ChatData(String nickname, String message, String profilePic, String key) {
        this.nickname = nickname;
        this.message = message;
        this.profilePic = profilePic;
        this.key = key;
    }

    public ChatData() { }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfilePic() { return profilePic; }

    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }
}
