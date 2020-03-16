package com.example.mychat;

public class User {

    public String name, about, email, phone;
    public int gender;

    public User()
    {

    }


    public User(String name, String about,String email, String phone, int gender)
    {
        this.name = name;
        this.about = about;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
    }

    public User(String name, String email, String phone, int gender)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }
}
