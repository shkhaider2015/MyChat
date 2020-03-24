package com.example.mychat.Models;

import java.io.Serializable;

public class MessageModel implements Serializable {

    String id, name, message_data, date, image_uri;

    public MessageModel()
    {

    }

    public MessageModel(String id, String name, String message_data, String date, String image_uri) {
        this.id = id;
        this.name = name;
        this.message_data = message_data;
        this.date = date;
        this.image_uri =image_uri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMessage_data() {
        return message_data;
    }

    public String getDate() {
        return date;
    }

    public String getImage_uri() {
        return image_uri;
    }
}
