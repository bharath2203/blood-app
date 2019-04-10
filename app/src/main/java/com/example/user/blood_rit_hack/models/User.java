package com.example.user.blood_rit_hack.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    public String name;
    public String email;
    public String phone_no;
    public String user_id;
    public String blood_group;
    public boolean is_user_requested;
    public String request_text;
    public double latitude, longitude;

    public User() {

    }


    public User(String name, String email, String phone_no, String user_id, String blood_group, double latitude, double longitude) {
        this.name = name;
        this.email = email;
        this.phone_no = phone_no;
        this.user_id = user_id;
        this.blood_group = blood_group;
        this.is_user_requested = false;
        this.request_text = "In urgent need of Blood...";
        this.longitude = longitude;
        this.latitude = latitude;
    }

    String getName() {
        return name;
    }

    String getEmail() {
        return email;
    }

    String getPhone_no() {
        return phone_no;
    }



    String getBlood_group() {
        return blood_group;
    }

    String getUser_id() {
        return user_id;
    }
}
