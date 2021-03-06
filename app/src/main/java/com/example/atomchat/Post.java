package com.example.atomchat;

public class Post {
    private String Author;
    private String Date;
    private String Text;
    private String ImageURL;
    private String key;

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Post(String author, String date, String text, String ImageURL, String key) {
        this.Author = author;
        this.Date = date;
        this.Text = text;
        this.ImageURL = ImageURL;
        this.key = key;
    }
}
