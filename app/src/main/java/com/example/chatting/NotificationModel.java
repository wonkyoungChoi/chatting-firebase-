package com.example.chatting;

public class NotificationModel {
    public String to;
    public Notification notification;

    public static class Notification {
        public String title;
        public String text;
    }
}
