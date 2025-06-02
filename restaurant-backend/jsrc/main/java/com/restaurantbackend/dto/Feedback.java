package com.restaurantbackend.dto;
import org.json.JSONObject;
import java.time.LocalDate;

public record Feedback(
        String reservationId,
        String serviceRating,
        String serviceComment,
        String cuisineRating,
        String cuisineComment
) {
    public static Feedback fromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);

        String reservationId = json.optString("reservationId", null);
        String serviceRating = json.optString("serviceRating", null);
        String serviceComment = json.optString("serviceComment", null);
        String cuisineRating = json.optString("cuisineRating", null);
        String cuisineComment = json.optString("cuisineComment", null);

        return new Feedback(reservationId,serviceRating,serviceComment,cuisineRating,cuisineComment);
    }
}
