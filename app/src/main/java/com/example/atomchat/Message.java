package com.example.atomchat;

public class Message {
    public String userName;
    public String textMessage;

    public Message(){}
    public Message(String textMessage){
        this.textMessage = textMessage;
        //this.userName = userName;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public String getUserName() {
        return userName;
    }
}
