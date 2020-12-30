package com.example.chatting;

import java.io.Serializable;

public class ChatData{
    private String massage;
    private String nickname;

    public ChatData(String nickname, String massage) {
        this.nickname = nickname;
        this.massage = massage;
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
}
