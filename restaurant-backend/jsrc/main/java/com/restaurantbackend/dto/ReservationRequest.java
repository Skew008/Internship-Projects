package com.restaurantbackend.dto;

import org.json.JSONObject;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ReservationRequest {
    private String id;
    private String locationId;
    private String tableNumber;  // ✅ Changed to String (as received in JSON)
    private String date;
    private String guestsNumber;  // ✅ Changed to String (as received in JSON)
    private String timeFrom;
    private String timeTo;

    public String getId() {
        return id;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public String getDate() {
        return date;
    }

    public String getGuestsNumber() {
        return guestsNumber;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public static ReservationRequest fromJson(String json) {
        JSONObject obj = new JSONObject(json);
        ReservationRequest request = new ReservationRequest();
        request.id = obj.optString("id", null);
        request.locationId = obj.getString("locationId");
        request.tableNumber = obj.getString("tableNumber");  // Accepting tableNumber as String
        request.date = obj.getString("date");
        request.guestsNumber = obj.getString("guestsNumber");  // Accepting guestsNumber as String
        request.timeFrom = obj.getString("timeFrom");
        request.timeTo = obj.getString("timeTo");

        return request;
    }

    public static String formatTimeTo24hr(String time) {

        time = time.toUpperCase();

        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("h:mm a");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("HH:mm");

        return LocalTime.parse(time, inputFormat).format(outputFormat);
    }

    public static String formatTimeToAmPm(String time) {

        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("h:mm a");
        DateTimeFormatter  inputFormat = DateTimeFormatter.ofPattern("HH:mm");

        return LocalTime.parse(time, inputFormat).format(outputFormat);
    }
}
