package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.restaurantbackend.Enum.FeedbackType;
import com.restaurantbackend.Enum.ReservationStatus;
import com.restaurantbackend.dto.ReservationRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReservationService {

    private final AmazonDynamoDB dynamoDB;
    private final String reservationsTable;
    private final String feedbacksTable;
    private final String orderTable;


    public ReservationService(AmazonDynamoDB dynamoDB) {
        if(dynamoDB==null){
            throw new IllegalArgumentException("dynamoDB is null");
        }
        this.dynamoDB = dynamoDB;
        this.reservationsTable = System.getenv("RESERVATIONS_TABLE");
        this.feedbacksTable=System.getenv("FEEDBACKS_TABLE");
        this.orderTable = System.getenv("ORDERS_TABLE");
    }

    public List<Map<String, AttributeValue>> getReservations(String email)
    {
        if(email==null || email.isEmpty()){
            throw new IllegalArgumentException("email is empty");
        }
        //need email from access tokens
        ScanRequest scanRequest = new ScanRequest().
                withTableName(reservationsTable).
                withFilterExpression("email = :emailid").
                withExpressionAttributeValues(Map.of(":emailid", new AttributeValue().withS(email)));//put the email

        return dynamoDB.scan(scanRequest).getItems();
    }
    public List<Map<String, AttributeValue>> getReservationsOfWaiter(String waiterEmail, String date ,String timeFrom, String tableNumber,Context context)
    {
        updateReservationStatuses(context);
        if(waiterEmail==null || waiterEmail.isEmpty()){
            throw new IllegalArgumentException("waiter email is empty");
        }
        if(date==null || date.isEmpty())
        {
            date=LocalDate.now().toString();
        }
        StringBuilder filterExpression=new StringBuilder("waiterEmail = :waiterEmail AND #dt = :date");
        Map<String,String> expressionAttributeName=Map.of("#dt","date");
        Map<String,AttributeValue> expressionAttributeValue=new HashMap<>();
        expressionAttributeValue.put(":waiterEmail",new AttributeValue().withS(waiterEmail));
        expressionAttributeValue.put(":date",new AttributeValue().withS(date));
         if(!timeFrom.equalsIgnoreCase("Any time"))
        {
            filterExpression.append(" AND timeFrom = :timeFrom");
            expressionAttributeValue.put(":timeFrom",new AttributeValue().withS(ReservationRequest.formatTimeTo24hr(timeFrom)));
        }
        if(!tableNumber.equalsIgnoreCase("Any table"))
        {
            filterExpression.append(" AND tableNumber = :tableNumber");
            expressionAttributeValue.put(":tableNumber",new AttributeValue().withS(tableNumber));
        }
        //need email from access tokens
        ScanRequest scanRequest = new ScanRequest().
                withTableName(reservationsTable).
                withFilterExpression(filterExpression.toString()).
                withExpressionAttributeNames(expressionAttributeName).
                withExpressionAttributeValues(expressionAttributeValue);//put the email

        return dynamoDB.scan(scanRequest).getItems();
    }

    public void deleteReservationbyId(String id)
    {
        if(id==null || id.isEmpty()){
            throw new IllegalArgumentException("id is not present");
        }
        Map<String, AttributeValue> key = Map.of(
                "id", new AttributeValue().withS(id)
        );

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(reservationsTable)
                .withKey(key);

        var reservation = dynamoDB.getItem(getItemRequest).getItem();

        if(!checkReservationValidForCancellation(reservation)) {
            throw new RuntimeException("Sorry you can only cancel reservation 30 min prior to you time");
        }

        UpdateItemRequest request=new UpdateItemRequest()
                .withTableName(reservationsTable)
                .withKey(key)
                .withUpdateExpression("SET #status = :status")
                .withExpressionAttributeNames(Map.of("#status", "status"))  // Alias for reserved keywords
                .withExpressionAttributeValues(Map.of(":status", new AttributeValue().withS(ReservationStatus.CANCELLED.name())));
        dynamoDB.updateItem(request);

        DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName(feedbacksTable)
                .withKey(Map.of("id", new AttributeValue().withS(reservation.get("feedbackId").getS()), "type", new AttributeValue().withS(FeedbackType.CUISINE.name())));
        dynamoDB.deleteItem(deleteItemRequest);
        deleteItemRequest = new DeleteItemRequest()
                .withTableName(feedbacksTable)
                .withKey(Map.of("id", new AttributeValue().withS(reservation.get("feedbackId").getS()), "type", new AttributeValue().withS(FeedbackType.SERVICE.name())));
        dynamoDB.deleteItem(deleteItemRequest);

        Map<String, AttributeValue> order = Map.of(":id", new AttributeValue().withS(reservation.getOrDefault("orderId", new AttributeValue().withS("-1")).getS()));
        List<Map<String, AttributeValue>> orderItems = dynamoDB.query(new QueryRequest().withTableName(orderTable).withKeyConditionExpression("id = :id").withExpressionAttributeValues(order)).getItems();
        if(orderItems!=null)
        {
            for(Map<String, AttributeValue> dish: orderItems)
            {
                dynamoDB.deleteItem(
                        new DeleteItemRequest()
                                .withTableName(orderTable)
                                .withKey(Map.of("id", new AttributeValue().withS(reservation.get("orderId").getS()), "dishId", new AttributeValue().withS(dish.get("dishId").getS())))
                );
            }
            updatePreOrder(0, id);

        }
    }

    public boolean checkReservationValidForCancellation(Map<String, AttributeValue> reservation) {
        String status = reservation.get("status").getS();
        if(status.equals("IN_PROGRESS")) throw new RuntimeException("Reservation is already under progress");
        if(status.equals("FINISHED")) throw new RuntimeException("Reservation is already finished");

        String dateStr = reservation.get("date").getS();
        String timeFromStr = reservation.get("timeFrom").getS();

        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime timeFrom = LocalTime.parse(timeFromStr, DateTimeFormatter.ofPattern("HH:mm"));

        LocalDateTime reservationStart = LocalDateTime.of(date, timeFrom);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        return now.isBefore(reservationStart.minusMinutes(30));
    }
    public  boolean isReservationInFuture(String timeToStr, String dateStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Parse the date and time
        LocalDate reservationDate = LocalDate.parse(dateStr, dateFormatter);
        LocalTime timeTo = LocalTime.parse(timeToStr, timeFormatter);
        LocalDateTime endDateTime = LocalDateTime.of(reservationDate, timeTo);

        // Get current time in IST
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Reservation must be in the future
        return endDateTime.isAfter(now);
    }
    private boolean isValidSlot(String timeFrom, String timeTo) {
        List<String> allowedSlots = List.of(
                "10:30-12:00",
                "12:15-13:45",
                "14:00-15:30",
                "15:45-17:15",
                "17:30-19:00",
                "19:15-20:45",
                "21:00-22:30"
        );

        String slot = timeFrom + "-" + timeTo;
        return allowedSlots.contains(slot);
    }
    private boolean isOverlappingReservation(ReservationRequest request) {
        String tableNumber = request.getTableNumber();
        String date = request.getDate();
        String locationId = request.getLocationId();
        String newTimeFromStr = ReservationRequest.formatTimeTo24hr(request.getTimeFrom());
        String newTimeToStr = ReservationRequest.formatTimeTo24hr(request.getTimeTo());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime newStart = LocalTime.parse(newTimeFromStr, timeFormatter);
        LocalTime newEnd = LocalTime.parse(newTimeToStr, timeFormatter);

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(reservationsTable)
                .withFilterExpression("tableNumber = :tableNumber AND #d = :date AND locationId = :locationId")
                .withExpressionAttributeNames(Map.of("#d", "date"))
                .withExpressionAttributeValues(Map.of(
                        ":tableNumber", new AttributeValue().withS(tableNumber),
                        ":date", new AttributeValue().withS(date),
                        ":locationId", new AttributeValue().withS(locationId)
                ));

        List<Map<String, AttributeValue>> existingReservations = dynamoDB.scan(scanRequest).getItems();

        for (Map<String, AttributeValue> reservation : existingReservations) {
            String status = reservation.get("status").getS();
            if (status.equals("CANCELLED") || status.equals("FINISHED")) continue;

            LocalTime existingStart = LocalTime.parse(reservation.get("timeFrom").getS(), timeFormatter);
            LocalTime existingEnd = LocalTime.parse(reservation.get("timeTo").getS(), timeFormatter);

            boolean isOverlap = newStart.equals(existingStart) && newEnd.equals(existingEnd)
                    || (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));
            if (isOverlap) return true;
        }

        return false;
    }

    public String allotWaiter(String locationId,String date,String timeFrom,List<String> allWaiters)
    {

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(reservationsTable)
                .withFilterExpression("timeFrom = :timeFrom AND #d = :date AND locationId = :locationId")
                .withExpressionAttributeNames(Map.of("#d", "date"))
                .withExpressionAttributeValues(Map.of(
                        ":timeFrom", new AttributeValue().withS(ReservationRequest.formatTimeTo24hr(timeFrom)),
                        ":date", new AttributeValue().withS(date),
                        ":locationId", new AttributeValue().withS(locationId)
                ));
        List<Map<String, AttributeValue>> waiterPresentInReservationTable = dynamoDB.scan(scanRequest).getItems();


        Map<String, Long> initialWaiterWork = waiterPresentInReservationTable==null ? new HashMap<>() : waiterPresentInReservationTable.stream().map(item -> item.get("waiterEmail").getS()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<String,Long> allWaiterWithAssignedTableCount=new HashMap<>();
       for(String waiterEmail:allWaiters)
       {

           allWaiterWithAssignedTableCount.put(waiterEmail,initialWaiterWork.getOrDefault(waiterEmail,0L));

       }
      return  allWaiterWithAssignedTableCount.entrySet().stream().min((entry1,entry2)->Long.compare(entry1.getValue(),entry2.getValue())).get().getKey();

    }

    public Map<String, AttributeValue> createReservation(ReservationRequest reservation, String email,String allotedWaiter, boolean byWaiter) {
        String reservationId = UUID.randomUUID().toString();
        String feedbackId = createFeedbackEntry(reservationId); // Create feedback entry
        if (!isValidSlot(ReservationRequest.formatTimeTo24hr(reservation.getTimeFrom()), ReservationRequest.formatTimeTo24hr(reservation.getTimeTo()))) {
            throw new IllegalArgumentException("Invalid time slot. Please select one of the allowed slots.");
        }

        if (isOverlappingReservation(reservation)) {
            throw new IllegalArgumentException("Table is already booked for this time slot.");
        }

        if(!isReservationInFuture(ReservationRequest.formatTimeTo24hr(reservation.getTimeTo()),reservation.getDate()))
        {
            throw new IllegalArgumentException("Reservation time is already in past!");
        }
        Map<String, AttributeValue> reservationItem = new HashMap<>();
        reservationItem.put("id", new AttributeValue().withS(reservationId));
        reservationItem.put("locationId", new AttributeValue().withS(reservation.getLocationId()));
        reservationItem.put("tableNumber", new AttributeValue().withS(reservation.getTableNumber()));
        reservationItem.put("date", new AttributeValue().withS(reservation.getDate()));
        reservationItem.put("guestsNumber", new AttributeValue().withS(reservation.getGuestsNumber()));
        reservationItem.put("timeFrom", new AttributeValue().withS(ReservationRequest.formatTimeTo24hr(reservation.getTimeFrom())));
        reservationItem.put("timeTo", new AttributeValue().withS(ReservationRequest.formatTimeTo24hr(reservation.getTimeTo())));
        reservationItem.put("status", new AttributeValue().withS(ReservationStatus.RESERVED.name())); // Default status
        reservationItem.put("email",new AttributeValue().withS(email));
        // Set preOrder as empty string since it's not in the request
        reservationItem.put("preOrder", new AttributeValue().withS("0"));

        reservationItem.put("feedbackId", new AttributeValue().withS(feedbackId)); // Storing feedback ID
        reservationItem.put("waiterEmail",new AttributeValue().withS(allotedWaiter));
        reservationItem.put("byWaiter", new AttributeValue().withBOOL(byWaiter));

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(reservationsTable)
                .withItem(reservationItem);

        dynamoDB.putItem(putItemRequest);
        return reservationItem;
    }

    public String createFeedbackEntry(String reservationId) {
        String feedbackId = UUID.randomUUID().toString();

        Map<String, AttributeValue> feedbackItem = new HashMap<>();
        feedbackItem.put("id", new AttributeValue().withS(feedbackId));
        feedbackItem.put("reservationId", new AttributeValue().withS(reservationId));
        feedbackItem.put("type", new AttributeValue().withS(FeedbackType.CUISINE.name()));

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(feedbacksTable)
                .withItem(feedbackItem);

        dynamoDB.putItem(putItemRequest);

        feedbackItem.put("type", new AttributeValue().withS(FeedbackType.SERVICE.name()));

        putItemRequest = new PutItemRequest()
                .withTableName(feedbacksTable)
                .withItem(feedbackItem);

        dynamoDB.putItem(putItemRequest);

        return feedbackId;
    }

    public void updateReservationStatuses(Context context) {
        try {
            ScanRequest scanRequest = new ScanRequest().withTableName(reservationsTable);
            ScanResult scanResult = dynamoDB.scan(scanRequest);
            if(scanResult.getItems().isEmpty()) return;

            for (Map<String, AttributeValue> item : scanResult.getItems()) {
                String reservationId = item.get("id").getS();
                String date = item.get("date").getS();
                String timeFrom = item.get("timeFrom").getS();
                String timeTo = item.get("timeTo").getS();
                String status = item.get("status").getS();
                if(status.equals("CANCELLED")) continue;

                ReservationStatus newStatus = determineStatus(timeFrom, timeTo, date, status);
                if(!newStatus.toString().equals(status)) updateReservationStatus(reservationId, newStatus);
            }
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            System.err.println("Error updating reservation statuses: " + e.getMessage());
        }
    }

    // Determines the reservation status based on current time
    private ReservationStatus determineStatus(String timeFromStr, String timeToStr, String dateStr, String currentStatus) {
        if(currentStatus.equals("FINISHED")) return ReservationStatus.FINISHED;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Parse the date and time strings
        LocalDate reservationDate = LocalDate.parse(dateStr, dateFormatter);
        LocalTime timeFrom = LocalTime.parse(timeFromStr, timeFormatter);
        LocalTime timeTo = LocalTime.parse(timeToStr, timeFormatter);

        LocalDateTime startDateTime = LocalDateTime.of(reservationDate, timeFrom);
        LocalDateTime endDateTime = LocalDateTime.of(reservationDate, timeTo);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Logic
        if (now.isAfter(startDateTime)) {
            return ReservationStatus.IN_PROGRESS;
        } else {
            return ReservationStatus.RESERVED;
        }
    }

    // Updates the status of a reservation in DynamoDB
    private void updateReservationStatus(String reservationId, ReservationStatus newStatus) {
        try {
            Map<String, AttributeValue> key = Map.of("id", new AttributeValue().withS(reservationId));
            Map<String, AttributeValueUpdate> updates = Map.of(
                    "status", new AttributeValueUpdate().withValue(new AttributeValue().withS(newStatus.name())).withAction(AttributeAction.PUT)
            );

            UpdateItemRequest updateRequest = new UpdateItemRequest()
                    .withTableName(reservationsTable)
                    .withKey(key)
                    .withAttributeUpdates(updates);

            dynamoDB.updateItem(updateRequest);
        } catch (Exception e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
        }
    }

    public List<Map<String, AttributeValue>> getAllFeedbacksByLocation(String locationId)
    {
        ScanRequest scan = new ScanRequest()
                .withTableName(reservationsTable)
                .withFilterExpression("locationId = :locationId AND #stat = :status")
                .withExpressionAttributeValues(Map.of(":locationId", new AttributeValue().withS(locationId), ":status", new AttributeValue().withS(ReservationStatus.FINISHED.name()) ))
                .withExpressionAttributeNames(Collections.singletonMap("#stat", "status"));

        List<Map<String, AttributeValue>> items = dynamoDB.scan(scan).getItems();
        if(items==null)
            throw new IllegalArgumentException("Invalid location id or no feedbacks yet");
        return items;
    }


    public Map<String, AttributeValue> getReservationById(String id) {

        Map<String, AttributeValue> reservation = dynamoDB.getItem(new GetItemRequest()
                        .withTableName(reservationsTable)
                        .withKey(Map.of("id", new AttributeValue().withS(id))))
                .getItem();

        if (reservation==null)
            throw new IllegalArgumentException("Reservation Id does not exist");
        return reservation;
    }

    public void putOrderId(String id) {

        try {
            String orderId = UUID.randomUUID().toString();
            dynamoDB.updateItem(
                    new UpdateItemRequest()
                            .withTableName(reservationsTable)
                            .withKey(Map.of("id", new AttributeValue().withS(id)))
                            .withConditionExpression("attribute_not_exists(orderId)")
                            .withUpdateExpression("SET orderId = :orderId")
                            .withExpressionAttributeValues(Map.of(":orderId", new AttributeValue().withS(orderId)))
            );

        } catch (Exception ignored) {
        }

    }

    public List<Map<String, AttributeValue>> getReservationByEmail(String email) {

        return dynamoDB.scan(
                new ScanRequest()
                        .withTableName(reservationsTable)
                        .withFilterExpression("email = :email AND attribute_exists(orderId) AND #stat = :status")
                        .withExpressionAttributeValues(Map.of(":email", new AttributeValue().withS(email), ":status", new AttributeValue().withS(ReservationStatus.RESERVED.name())))
                        .withExpressionAttributeNames(Collections.singletonMap("#stat", "status"))
        ).getItems();
    }

    public void updateReservationStatusById(String id) {

        Map<String, AttributeValue> item = dynamoDB.getItem(
                new GetItemRequest()
                        .withTableName(reservationsTable)
                        .withKey(Map.of("id", new AttributeValue().withS(id)))
        ).getItem();

        String reservationId = item.get("id").getS();
        String date = item.get("date").getS();
        String timeFrom = item.get("timeFrom").getS();
        String timeTo = item.get("timeTo").getS();
        String status = item.get("status").getS();
        if(status.equals("CANCELLED")) return;

        ReservationStatus newStatus = determineStatus(timeFrom, timeTo, date, status);
        if(!newStatus.toString().equals(status)) updateReservationStatus(reservationId, newStatus);

    }

    public void updatePreOrder(int preOrder, String id) {
        if(preOrder==0)
        {
            dynamoDB.updateItem(
                    new UpdateItemRequest()
                            .withTableName(reservationsTable)
                            .withKey(Map.of("id", new AttributeValue().withS(id)))
                            .withUpdateExpression("REMOVE orderId SET preOrder = :pre")
                            .withExpressionAttributeValues(Map.of(":pre", new AttributeValue().withS("0")))
            );
        }

            dynamoDB.updateItem(
                    new UpdateItemRequest()
                            .withTableName(reservationsTable)
                            .withKey(Map.of("id", new AttributeValue().withS(id)))
                            .withUpdateExpression("SET preOrder = :ct")
                            .withExpressionAttributeValues(Map.of(":ct", new AttributeValue().withS(String.valueOf(preOrder))))
            );
    }

    public Map<String, AttributeValue> updateReservation(ReservationRequest reservation) {

        if (!isValidSlot(ReservationRequest.formatTimeTo24hr(reservation.getTimeFrom()), ReservationRequest.formatTimeTo24hr(reservation.getTimeTo()))) {
            throw new IllegalArgumentException("Invalid time slot. Please select one of the allowed slots.");
        }

        if (isOverlappingReservation(reservation) && !sameSLot(reservation)) {
            throw new IllegalArgumentException("Table is already booked for this time slot.");
        }

        if(!isReservationInFuture(ReservationRequest.formatTimeTo24hr(reservation.getTimeTo()),reservation.getDate()))
        {
            throw new IllegalArgumentException("Reservation time is already in past!");
        }

        try {
            dynamoDB.updateItem(
                    new UpdateItemRequest()
                            .withTableName(reservationsTable)
                            .withKey(Map.of("id", new AttributeValue().withS(reservation.getId())))
                            .withUpdateExpression("SET tableNumber = :tn, #dt = :dt, timeFrom = :tf, timeTo = :tt, guestsNumber = :gn")
                            .withExpressionAttributeValues(Map.of(":tn", new AttributeValue().withS(reservation.getTableNumber()), ":dt", new AttributeValue().withS(reservation.getDate()), ":tt", new AttributeValue().withS(ReservationRequest.formatTimeTo24hr(reservation.getTimeTo())), ":tf", new AttributeValue().withS(ReservationRequest.formatTimeTo24hr(reservation.getTimeFrom())), ":gn", new AttributeValue().withS(reservation.getGuestsNumber())))
                            .withExpressionAttributeNames(Map.of("#dt", "date"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Guest capacity exceeded for the table");
        }

        return dynamoDB.getItem(
                new GetItemRequest()
                        .withTableName(reservationsTable)
                        .withKey(Map.of("id", new AttributeValue().withS(reservation.getId())))
        ).getItem();
    }

    private boolean sameSLot(ReservationRequest request) {

        Map<String, AttributeValue> reservation = getReservationById(request.getId());

        return reservation.get("date").getS().equals(request.getDate()) && reservation.get("timeFrom").getS().equals(ReservationRequest.formatTimeTo24hr(request.getTimeFrom())) && reservation.get("timeTo").getS().equals(ReservationRequest.formatTimeTo24hr(request.getTimeTo()));
    }
}
