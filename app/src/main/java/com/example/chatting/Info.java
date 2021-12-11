package com.example.chatting;

import java.io.Serializable;

public class Info implements Serializable {
    String email;
    String uid;
    Boolean check;

    public Info(String email, String uid, Boolean check) {
        this.email = email;
        this.uid = uid;
        this.check = check;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }
}
