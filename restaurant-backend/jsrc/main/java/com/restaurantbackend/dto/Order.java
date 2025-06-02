package com.restaurantbackend.dto;

import org.json.JSONArray;
import org.json.JSONObject;

public record Order(String address, String date, JSONArray dishItems, String id, String reservationId, String timeSlot) {

    public static Order fromJSON(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        String address = jsonObject.getString("address");
        String date = jsonObject.getString("date");
        JSONArray dishItems = new JSONArray(jsonObject.getJSONArray("dishItems"));
        String id = jsonObject.getString("id");
        String reservationId =  jsonObject.getString("reservationId");
        String timeSlot = jsonObject.getString("timeSlot");

        return new Order(address,date,dishItems,id,reservationId,timeSlot);
    }
}
