package com.task11.dto;

import org.json.JSONObject;

import java.util.Date;

public final class Reservations {

    private final int tableNumber;
    private final String clientName;
    private final String phoneNumber;
    private final String  date; //yyyy-MM-dd format
    private final String slotTimeStart; //"HH:MM" format
    private final String slotTimeEnd; //"HH:MM" format

    public Reservations(int tableNumber, String clientName, String phoneNumber, String date, String slotTimeStart, String slotTimeEnd) {
        this.tableNumber = tableNumber;
        this.clientName = clientName;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.slotTimeStart = slotTimeStart;
        this.slotTimeEnd = slotTimeEnd;
    }

    public static Reservations fromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        int tableNumber = json.optInt("tableNumber", -1);
        String clientName = json.optString("clientName", null);
        String phoneNumber = json.optString("phoneNumber", null);
        String date = json.optString("date", null);
        String slotTimeStart = json.optString("slotTimeStart", null);
        String slotTimeEnd = json.optString("slotTimeEnd", null);

        return new Reservations(tableNumber, clientName, phoneNumber, date, slotTimeStart, slotTimeEnd);
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public String getSlotTimeStart() {
        return slotTimeStart;
    }

    public String getSlotTimeEnd() {
        return slotTimeEnd;
    }
}
