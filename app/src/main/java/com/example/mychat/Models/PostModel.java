package com.example.mychat.Models;

import java.io.Serializable;

public class PostModel implements Serializable
{
    public String postDetail;
    public String userName;
    public String userImageUrl;
    public String date;
    public String id;

    public PostModel()
    {

    }
    public PostModel(String userName, String userImageUrl, String date, String postDetail, String id)
    {
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.date = date;
        this.postDetail = postDetail;
        this.id = id;
    }

    public String getPostDetail() {
        return postDetail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPostDetail(String postDetail) {
        this.postDetail = postDetail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
