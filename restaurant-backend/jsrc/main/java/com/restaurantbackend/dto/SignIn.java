package com.restaurantbackend.dto;

import org.json.JSONObject;

public record SignIn(String email, String password){
    public static SignIn fromJson (String jsonString){
        JSONObject json = new JSONObject(jsonString);
        String email = json.optString("email", null).toLowerCase();
        String password = json.optString("password", null);
        return new SignIn(email,password);
    }
}
