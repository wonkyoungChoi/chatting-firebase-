package com.example.chatting;

public class Member {
    private String name;
    private String nickname;
    private String phone;
    private String birth;
    private String profilePic;
    private Boolean check;
    private String email;
    private String chattingRoom;
    private String fcmToken;
    private String otherUid;

    public Member(String email, String name, String nickname, String phone, String birth, String profilePic, Boolean check, String chattingRoom, String fcmToken, String otherUid) {
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.birth = birth;
        this.profilePic = profilePic;
        this.check = check;
        this.email = email;
        this.chattingRoom = chattingRoom;
        this.fcmToken = fcmToken;
        this.otherUid = otherUid;
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

    public String getProfilePic() { return profilePic; }

    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChattingRoom() {
        return chattingRoom;
    }

    public void setChattingRoom(String chattingRoom) {
        this.chattingRoom = chattingRoom;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getOtherUid() {
        return otherUid;
    }

    public void setOtherUid(String otherUid) {
        this.otherUid = otherUid;
    }
}
