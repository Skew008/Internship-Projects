package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.restaurantbackend.dto.ReservationRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TablesService {

    private final AmazonDynamoDB dynamoDB;
    private final String tablesTable;
    private final String reservationTable;

    public TablesService(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        this.tablesTable = System.getenv("TABLES_TABLE");
        this.reservationTable = System.getenv("RESERVATIONS_TABLE");
    }

    public List<Map<String, AttributeValue>> getAllTablesByLocationAndCapacity(String locationId, String capacity) {
        Map<String, String> attributeNames = Map.of("#cap", "capacity");
        String filterExpression = "#cap >= :cap";
        if(locationId==null)
        {
            ScanRequest scanRequest = new ScanRequest()
                    .withTableName(tablesTable)
                    .withFilterExpression(filterExpression)
                    .withExpressionAttributeNames(attributeNames)
                    .withExpressionAttributeValues(Map.of(":cap", new AttributeValue().withS(capacity)));

            return dynamoDB.scan(scanRequest).getItems();
        }
        else
        {
            QueryRequest queryRequest = new QueryRequest()
                    .withTableName(tablesTable)
                    .withKeyConditionExpression("locationId = :locId")
                    .withExpressionAttributeValues(
                            Map.of(
                                    ":locId", new AttributeValue().withS(locationId),
                                    ":cap", new AttributeValue().withS(capacity)
                            )
                    )
                    .withFilterExpression(filterExpression)
                    .withExpressionAttributeNames(attributeNames);

            return dynamoDB.query(queryRequest).getItems();
        }
    }

    public List<String> getNotAvailableSlots(String tableNum, String locationId, String date) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(reservationTable)
                .withFilterExpression("locationId = :locId AND tableNumber = :tableNo AND #dt = :date")
                .withExpressionAttributeValues(
                        Map.of(
                                ":locId", new AttributeValue().withS(locationId),
                                ":tableNo", new AttributeValue().withS(tableNum),
                                ":date", new AttributeValue().withS(date)
                        )
                )
                .withExpressionAttributeNames(Map.of("#dt", "date"));

        return dynamoDB.scan(scanRequest)
                .getItems()
                .stream()
                .filter(item -> {
                    String status = item.get("status").getS();
                    return status.equals("RESERVED") || status.equals("IN_PROGRESS") || status.equals("FINISHED");
                })
                .map(item -> item.get("timeFrom").getS() + "-" + item.get("timeTo").getS())
                .toList();
    }

    public List<String> getAllSlots(String time) {
        List<String> slots = new ArrayList<>();
        slots.add("10:30-12:00");
        slots.add("12:15-13:45");
        slots.add("14:00-15:30");
        slots.add("15:45-17:15");
        slots.add("17:30-19:00");
        slots.add("19:15-20:45");
        slots.add("21:00-22:30");
        return slots.stream()
                .filter(obj->obj.split("-")[0].compareTo(ReservationRequest.formatTimeTo24hr(time)) >= 0)
                .collect(Collectors.toList());
    }

    public void removePastTimeSlots(List<String> slots, String date) {
        LocalDate givenDate = LocalDate.parse(date); // input format: yyyy-MM-dd
        LocalDate currentDate = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        if (!currentDate.equals(givenDate)) return;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));

        Iterator<String> iterator = slots.iterator();

        while (iterator.hasNext()) {
            String slot = iterator.next();
            String[] times = slot.split("-");
            LocalTime startTime = LocalTime.parse(times[0].trim(), timeFormatter);
            LocalTime endTime = LocalTime.parse(times[1].trim(), timeFormatter);

            // Remove slot if it is in the past or currently ongoing
            if (endTime.isBefore(now) || (now.isAfter(startTime) && now.isBefore(endTime)) || now.equals(startTime)) {
                iterator.remove();
            }
        }
    }

    public List<String> getFormattedSlots(List<String> slots) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("h:mm a");

        List<String> formattedSlots = new ArrayList<>();

        for (String slot : slots) {
            String[] parts = slot.split("-");
            LocalTime start = LocalTime.parse(parts[0], inputFormat);
            LocalTime end = LocalTime.parse(parts[1], inputFormat);

            String formatted = start.format(outputFormat) + " - " + end.format(outputFormat);
            formattedSlots.add(formatted);
        }
        return formattedSlots;
    }

    public void validTableDetails(ReservationRequest request) {


        Map<String, AttributeValue> item = dynamoDB.getItem(
                new GetItemRequest()
                        .withTableName(tablesTable)
                        .withKey(Map.of("locationId", new AttributeValue().withS(request.getLocationId()), "tableNumber", new AttributeValue().withS(request.getTableNumber())))
        ).getItem();
        if(item==null)
            throw new IllegalArgumentException("Table not present at this location");

        if(Integer.parseInt(request.getGuestsNumber())<0)
            throw new IllegalArgumentException("Guest number cannot be negative");

        if(Integer.parseInt(item.get("capacity").getS())<Integer.parseInt(request.getGuestsNumber()))
            throw new IllegalArgumentException("Guest number exceeded the table capacity");
    }
}
