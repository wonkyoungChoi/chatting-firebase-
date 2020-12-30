package com.example.chatting;

public class Member {
    private String name;
    private String nickname;
    private String phone;
    private String birth;

    public Member(String name, String nickname, String phone, String birth, String uid) {
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.birth = birth;
    }



    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirth() {
        return this.birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

}
