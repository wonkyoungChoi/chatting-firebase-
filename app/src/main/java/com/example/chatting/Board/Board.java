package com.example.chatting.Board;

public class Board {
    String title;
    String time;
    String text;
    String picture;

    public Board(String title, String time, String text, String picture) {
        this.title = title;
        this.time = time;
        this.text = text;
        this.picture = picture;
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
}

