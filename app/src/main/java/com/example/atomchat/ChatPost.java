package com.example.atomchat;

public class ChatPost {
    private String author;
    private String text;
    private String data;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ChatPost(String author, String data, String text, String imageURL, String key) {
        this.author = author;
        this.text = text;
        this.data = data;
        this.imageURL = imageURL;
        this.key = key;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    private String imageURL;
}
