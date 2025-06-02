package com.task11.dto;

import org.json.JSONObject;

import java.util.Optional;

public final class Tables {

    private final int id;
    private final int number;
    private final int places;
    private final boolean isVip;
    private final Optional<Integer> minOrder;


    public Tables(int id, int number, int places, boolean isVip, Optional<Integer> minOrder) {
        this.id = id;
        this.number = number;
        this.places = places;
        this.isVip = isVip;
        this.minOrder = minOrder;
    }

    public static Tables fromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        int id = json.optInt("id",-1);
        int number = json.optInt("number",-1);
        int places = json.optInt("places", -1);
        boolean isVip = json.optBoolean("isVip", false);
        Optional<Integer> minOrder = Optional.of(json.getInt("minOrder"));

        return new Tables(id, number, places, isVip, minOrder);
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getPlaces() {
        return places;
    }

    public boolean isVip() {
        return isVip;
    }

    public Optional<Integer> getMinOrder() {
        return minOrder;
    }
}
