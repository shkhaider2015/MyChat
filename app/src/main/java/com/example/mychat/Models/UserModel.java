package com.example.mychat.Models;

public class UserModel {
    public String name, about, phone, gender, email, imageUri;

    public UserModel(String name, String about, String phone, String gender, String email, String imageUri)
    {
        this.name = name;
        this.about = about;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.imageUri = imageUri;
    }

    public UserModel()
    {

    }

    public String getName() {
        return name;
    }

    public String getAbout() {
        return about;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUri() {
        return imageUri;
    }
}
