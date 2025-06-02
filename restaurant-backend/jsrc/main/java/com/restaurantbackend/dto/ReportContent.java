package com.restaurantbackend.dto;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public record ReportContent(String description , String downloadLink , String fromDateTime ,
                            String location , String name , String toDateTime , String waiter, List<Map<String, String>> items) {
    public static JSONObject toJson(ReportContent content){
        return new JSONObject()
                .put("description", content.description())
                .put("downloadLink" , content.downloadLink())
                .put("fromDate" , content.fromDateTime())
                .put("location" , content.location())
                .put("name" , content.name())
                .put("toDate" , content.toDateTime())
                .put("waiter" , content.waiter())
                .put("items" , content.items());
    }
}

