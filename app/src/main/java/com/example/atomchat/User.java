package com.example.atomchat;

public class User {
  private String id;
  private String username;
  private String profile_color;
  private String status;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getProfile_color() {
    return profile_color;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setProfile_color(String profile_color) {
    this.profile_color = profile_color;
  }

  public User(String id, String status) {
    this.id = id;
    this.username = userColor(id);
    this.profile_color = userColor(id);
    this.status = status;
  }
  public User(){}

  public String userColor(String id) {

    String color = "";
    String norm = "1234567890ABCDEFabcdef";
    int n = 0;
    for (int i = 0; i < id.length() && n < 6; i++) {
      for (int j = 0; j < 22; j++) {
        if (id.charAt(i) == norm.charAt(j)) {
          if (j < 16) color = color + norm.charAt(j);
          else color = color + norm.charAt(j - 6);
          n++;
        }
      }
    }
    while (n++ < 6) color = color + '0';
    color = "#" + color;
    return color;
  }
}