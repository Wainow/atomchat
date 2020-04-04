package com.example.atomchat;

public class Message {
    private String message;

    public Message(){}
    public Message(String message){
        this.message = message;
    }

    public void setTextMessage(String textMessage) {
        this.message = textMessage;
    }

    public String getTextMessage() {
        return message;
    }
}
