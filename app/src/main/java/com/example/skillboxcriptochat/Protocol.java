package com.example.skillboxcriptochat;

import com.google.gson.Gson;

public class Protocol {
    //1 - user status (online/offline)
    //2 - text message
    //3 - user name

    public final static int USER_STATUS = 1;
    public final static int MESSAGE = 2;
    public final static int USER_NAME = 3;
    public final static int GROUP_CHAT = 1;

    static class UserName{
        private String name;

        public UserName(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    //for incoming and outgoing
    static class Message{
        private long sender;
        private String encodedText;
        private long receiver;

        public Message(String encodedText) {
            this.encodedText = encodedText;
        }

        public String getEncodedText() {
            return encodedText;
        }

        public void setEncodedText(String encodedText) {
            this.encodedText = encodedText;
        }

        public long getReceiver() {
            return receiver;
        }

        public void setReceiver(long receiver) {
            this.receiver = receiver;
        }

        public long getSender() {
            return sender;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }
    }

    static class User{
        String name;
        private long id;

        public User() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    static class UserStatus{
        private boolean connected;
        private User user;

        public UserStatus() {
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    public static UserStatus unpackStatus(String json){
        Gson g = new Gson();
        return g.fromJson(json.substring(1), UserStatus.class);
    }

    public static String packMessage(Message m) {
        Gson g = new Gson();
        return MESSAGE + g.toJson(m);
    }

    public static Message unpackMessage(String json){
        Gson g = new Gson();
        return g.fromJson(json.substring(1), Message.class);
    }


    public static String packName(UserName name){
        Gson g = new Gson();
        return USER_NAME + g.toJson(name);
    }

    public static int getType(String json){
        if (json == null || json.length() == 0){
            return -1;
        }
        return Integer.parseInt(json.substring(0,1));
    }

}