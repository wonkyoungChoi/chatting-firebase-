package com.example.chatting;

import java.io.Serializable;

public class ChatData{
    private String massage;
    private String nickname;

    public String getMassage() {
        return massage;
    }

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
