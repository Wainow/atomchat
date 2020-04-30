package com.example.atomchat;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private String date;
    private String isseen;

    public String getIsseen() {
        return isseen;
    }

    public void setIsseen(String isseen) {
        this.isseen = isseen;
    }

    public Chat(String sender, String receiver, String message, String date, String isseen) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.date = date;
        this.isseen = isseen;
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
