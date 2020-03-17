package com.example.mychat.Models;

public class UserModel {
    String name, about, phone, gender, email, imageUri;

    public UserModel(String name, String about, String phone, String gender, String email, String imageUri)
    {
        this.name = name;
        this.about = about;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.imageUri = imageUri;
    }
}
