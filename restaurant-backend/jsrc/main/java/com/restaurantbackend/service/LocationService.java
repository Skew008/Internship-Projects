package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import org.json.JSONObject;

import java.util.*;


public class LocationService {

    private final AmazonDynamoDB dynamoDB;
    private final String locationsTable;

    public LocationService(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        this.locationsTable = System.getenv("LOCATIONS_TABLE");
    }

    public List<JSONObject> getAllLocations(){
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(locationsTable)).getItems();
        if( items==null || items.isEmpty()){
            throw new IllegalArgumentException("items array is empty");
        }

        return items.stream()
                .map(item -> new JSONObject()
                        .put("id", item.get("id").getS())
                        .put("address", item.get("address").getS())
                        .put("description", item.get("description").getS())
                        .put("totalCapacity", item.get("totalCapacity").getS())
                        .put("averageOccupancy", item.get("averageOccupancy").getS())
                        .put("imageUrl", item.get("imageUrl").getS())
                        .put("rating", item.get("rating").getS()))
                .toList();
    }


    public List<JSONObject> getAllLocationsBasedOnSelectOptions(){
        List<Map<String , AttributeValue>> items = dynamoDB.scan(new ScanRequest(locationsTable)).getItems();
        if( items==null || items.isEmpty()){
            throw new IllegalArgumentException("items array is empty");
        }

        return items.stream()
                .map(item -> new JSONObject()
                        .put("id", item.get("id").getS())
                        .put("address", item.get("address").getS()))
                .toList();
    }

    public String getLocationAddress(String locationId) {
        if(locationId==null || locationId.isEmpty()) {
            throw new IllegalArgumentException("Location id is not present");
        }
        Map<String, AttributeValue> expressionAttributeValues = Map.of(
                ":locationId", new AttributeValue().withS(locationId)
        );

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(locationsTable)
                .withFilterExpression("id = :locationId")
                .withExpressionAttributeValues(expressionAttributeValues);

        List<Map<String, AttributeValue>> items = dynamoDB.scan(scanRequest).getItems();

        if (items.isEmpty()) {
            return "Unknown Location"; // Default value if location not found
        }

        return items.get(0).get("address").getS();
    }
}
