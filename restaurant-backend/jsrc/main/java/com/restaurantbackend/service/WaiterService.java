package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WaiterService {
    private final AmazonDynamoDB dynamoDB;
    private final String waitersTable;
    private final String reservationTable;

    public WaiterService(AmazonDynamoDB dynamoDB) {

        if (dynamoDB == null) {
            throw new IllegalArgumentException("dynamoDB is null");
        }
        this.dynamoDB = dynamoDB;
        this.waitersTable = System.getenv("WAITERS_TABLE");
        this.reservationTable = System.getenv("RESERVATIONS_TABLE");
    }

    public List<String> getWaiters() {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(waitersTable);

        return dynamoDB.scan(scanRequest).getItems()
                .stream()
                .map(entry -> entry.get("email").getS())
                .collect(Collectors.toList());
    }

    public List<String> getWaiterByLocation(String locationId) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(waitersTable)
                .withFilterExpression("locationId = :locationId")
                .withExpressionAttributeValues(Map.of(":locationId", new AttributeValue().withS(locationId)));
        return dynamoDB.scan(scanRequest).getItems()
                .stream()
                .map(item -> item.get("email").getS())
                .collect(Collectors.toList());


    }

    public Map<String, AttributeValue> getWaiterById(String id) {
        return dynamoDB.getItem(
                new GetItemRequest()
                        .withTableName(waitersTable)
                        .withKey(Map.of("email", new AttributeValue().withS(id)))
        ).getItem();
    }

    public synchronized void updateRating(String email, String rating, String curRating) {

        Map<String, AttributeValue> waiter = dynamoDB.getItem(
                new GetItemRequest()
                        .withTableName(waitersTable)
                        .withKey(Map.of("email", new AttributeValue().withS(email)))
        ).getItem();

        Map<String, AttributeValue> attributeValueMap = null;
        if(Integer.parseInt(curRating)==-1)
        {
            attributeValueMap = Map.of(
                    ":pro", new AttributeValue().withN(
                            String.valueOf(Integer.parseInt(waiter.get("orderProcessed").getN()) + 1)
                    ),
                    ":tot", new AttributeValue().withN(
                            String.valueOf(Integer.parseInt(waiter.get("totalRating").getN()) + Integer.parseInt(rating))
                    )
            );
        }
        else
        {
            attributeValueMap = Map.of(
                    ":pro", new AttributeValue().withN(
                            waiter.get("orderProcessed").getN()
                    ),
                    ":tot", new AttributeValue().withN(
                            String.valueOf(Integer.parseInt(waiter.get("totalRating").getN()) + Integer.parseInt(rating) - Integer.parseInt(curRating))
                    )
            );
        }

        dynamoDB.updateItem(
                new UpdateItemRequest()
                        .withTableName(waitersTable)
                        .withUpdateExpression("SET orderProcessed = :pro, totalRating = :tot")
                        .withKey(Map.of("email", new AttributeValue().withS(email)))
                        .withExpressionAttributeValues(attributeValueMap)
        );

}
    public Map<String, AttributeValue> getWaiterDetailsByReservationId (String reservationId){
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(reservationTable)
                .withKeyConditionExpression("id = :reservationId")
                .withExpressionAttributeValues(Map.of(":reservationId", new AttributeValue().withS(reservationId)));
        List<Map<String, AttributeValue>> items = dynamoDB.query(queryRequest).getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No reservation with this id exist");
        }
        String waiterEmail = items.get(0).get("waiterEmail").getS();
        QueryRequest queryRequest1 = new QueryRequest()
                .withTableName(waitersTable)
                .withKeyConditionExpression("email = :waiterEmail")
                .withExpressionAttributeValues(Map.of(":waiterEmail", new AttributeValue().withS(waiterEmail)));
        List<Map<String, AttributeValue>> items1 = dynamoDB.query(queryRequest1).getItems();
        if (items1 == null || items1.isEmpty())
            throw new IllegalArgumentException("No waiter found for this this reservation");

        return items1.get(0);
    }
}