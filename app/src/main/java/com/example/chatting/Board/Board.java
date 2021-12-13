package com.example.chatting.Board;

public class Board {
    String key;
    String title;
    String time;
    String text;
    String picture;
    String timeKey;

    public Board(String key, String title, String time, String text, String picture, String timeKey) {
        this.key = key;
        this.title = title;
        this.time = time;
        this.text = text;
        this.picture = picture;
        this.timeKey = timeKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }
}

