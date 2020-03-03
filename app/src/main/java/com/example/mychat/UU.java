package com.example.mychat;

import android.net.Uri;

public class UU extends User {

    String phone;
    Uri profile;

    public UU()
    {

    }

    public UU(String name, String email, int gender, String phone, Uri profile)
    {
        super.name = name;
        super.email = email;
        super.gender = gender;
        this.phone = phone;
        this.profile = profile;

    }
}
