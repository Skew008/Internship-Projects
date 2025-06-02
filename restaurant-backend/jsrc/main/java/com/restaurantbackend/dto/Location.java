package com.restaurantbackend.dto;

public record Location(String id , String address
        , String description , String totalCapacity , String averageOccupancy ,
                       String imageUrl, String rating) {
}