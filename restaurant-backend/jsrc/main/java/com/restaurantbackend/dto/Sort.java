package com.restaurantbackend.dto;

import org.json.JSONObject;

public record Sort(String direction, String nullHandling, boolean ascending, String property, boolean ignoreCase) {

    public static JSONObject toJson(Sort sort) {
        return new JSONObject()
                .put("direction", sort.direction())
                .put("nullHandling", sort.nullHandling())
                .put("ascending", sort.ascending())
                .put("property", sort.property())
                .put("ignoreCase", sort.ignoreCase());
    }
}
