package com.example.atomchat;

import java.util.Date;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String date;

    public Chat(String sender, String receiver, String message, String date) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.date = date;
    }

    public Chat(){};

    public String getReceiver() {
        return receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
