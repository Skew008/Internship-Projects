package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.restaurantbackend.Enum.FeedbackType;
import com.restaurantbackend.Enum.ReservationStatus;
import com.restaurantbackend.dto.Feedback;
import com.restaurantbackend.dto.Pageable;
import com.restaurantbackend.dto.Sort;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FeedbackService {
    private final AmazonDynamoDB dynamoDB;
    private final String feedbackTable;
    private final String reservationsTable;
    private final AmazonSQS sqsClient;
    private final String queueUrl;

    public FeedbackService(AmazonDynamoDB dynamoDB, AmazonSQS sqsClient) {
        this.dynamoDB = dynamoDB;
        this.sqsClient = sqsClient;
        System.out.print(System.getenv("SQS_QUEUE"));
        System.out.print(System.getenv("ACCOUNT_ID"));
        this.queueUrl = sqsClient.getQueueUrl(new GetQueueUrlRequest()
                .withQueueName(System.getenv("SQS_QUEUE"))
                .withQueueOwnerAWSAccountId(System.getenv("ACCOUNT_ID"))
        ).getQueueUrl();
        this.feedbackTable = System.getenv("FEEDBACKS_TABLE");
        this.reservationsTable = System.getenv("RESERVATIONS_TABLE");
    }

//    public List<JSONObject> getFeedbacksContent(String type , List<String> feedbacksIds)
//    {
//        ScanRequest scanRequest = new ScanRequest(feedbackTable)
//                .withFilterExpression("locationId = :locationId")
//                .withExpressionAttributeValues(Map.of(":locationId" , new AttributeValue().withS(locationId)));
//
//        List<Map<String, AttributeValue>> items = amazonDynamoDB.scan(scanRequest).getItems();
//
//        ScanRequest scanRequestForType = new ScanRequest(typeTable)
//                .withFilterExpression("type = :type")
//                .withExpressionAttributeValues(Map.of(":type" , new AttributeValue().withS(type)));
//
//        List<Map<String, AttributeValue>> itemsForType = amazonDynamoDB.scan(scanRequestForType).getItems();
//
//        if(type.equals("SERVICE"))
//        {
//            return items.stream()
//                    .filter(item -> itemsForType.stream()
//                            .anyMatch(type_item -> type_item.get("type_id").equals(item.get("service_id"))))
//                     .map(item -> new JSONObject()
//                             .put("id" , item.get("id").getS())
//                             .put("reservationId" , item.get("reservationId").getS())
//                             .put("email" , item.get("email").getS())
//                             .put("date" , item.get("date").getS())
//                             .put("userName" , userName)
//                             .put("userAvatorUrl" , userAvatorUrl)
//                             .put("locationId" , locationId)
//                             .put("rate" , itemsForType.stream().filter(type_item -> type_item.get("type_id").equals(item.get("service_id"))).findFirst().get().get("rate").getS())
//                             .put("comment",itemsForType.stream().filter(type_item -> type_item.get("type_id").equals(item.get("service_id"))).findFirst().get().get("comment").getS())
//                     ).toList();
//        }
//        else
//        {
//            return items.stream()
//                    .filter(item -> itemsForType.stream()
//                            .anyMatch(type_item -> type_item.get("type_id").equals(item.get("cuisine_id"))))
//                    .map( item -> new JSONObject()
//                            .put("id" , item.get("id").getS())
//                            .put("reservationId" , item.get("reservationId").getS())
//                            .put("email" , item.get("email").getS())
//                            .put("date" , item.get("date").getS())
//                            .put("userName" , userName)
//                            .put("userAvatorUrl" , userAvatorUrl)
//                            .put("locationId" , locationId)
//                            .put("rate" , itemsForType.stream().filter(type_item -> type_item.get("type_id").equals(item.get("cuisine_id"))).findFirst().get().get("rate").getS())
//                            .put("comment",itemsForType.stream().filter(type_item -> type_item.get("type_id").equals(item.get("cuisine_id"))).findFirst().get().get("comment").getS())
//                    ).toList();
//        }
//    }

    public JSONObject getFeedbacksContent(String locationId, String type, String userName, String userAvatarUrl, String feedbacksId) {

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(feedbacksId));
        key.put("type", new AttributeValue().withS(type));
        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(feedbackTable)
                .withKey(key);

        Map<String, AttributeValue> item = dynamoDB.getItem(getItemRequest).getItem();
        return new JSONObject()
                .put("id", item.get("id").getS())
                .put("rate", item.getOrDefault("rate", new AttributeValue().withS(new String())).getS())
                .put("comment", item.getOrDefault("comment", new AttributeValue().withS(new String())).getS())
                .put("userName", userName)
                .put("userAvatarUrl", userAvatarUrl)
                .put("date", item.getOrDefault("date", new AttributeValue().withS("No feedback yet")).getS())
                .put("type", type)
                .put("locationId", locationId);
    }

    public List<JSONObject> getSortedFeedbacks(List<JSONObject> list, String sort) {
        String[] arr = sort.split(",");
        Comparator<JSONObject> comparator = Comparator.comparing((JSONObject o) -> o.getString(arr[0]));
        if (arr[1].equals("asc")) {
            list.sort(comparator);
        } else {
            list.sort(comparator.reversed());
        }
        return list;
    }

    public Sort getSort(String sort) {
        String[] arr = sort.split(",");
        Sort s = new Sort(arr[1],
                "NATIVE", arr[1].equals("asc"), arr[0], true);
        return s;
    }

    public Pageable getPagable(Integer page, String sort, Integer size) {
        Pageable p = new Pageable(0, getSort(sort), true, size, page, false);
        return p;
    }

    public String postFeedback(Feedback feedback) {
        if (feedback.reservationId() == null) {
            throw new IllegalArgumentException("Reservation ID is required.");
        }
        // Step 1: Fetch the reservation
        Map<String, AttributeValue> reservationKey = Map.of("id", new AttributeValue().withS(feedback.reservationId()));
        GetItemRequest getRequest = new GetItemRequest()
                .withTableName(reservationsTable)
                .withKey(reservationKey);

        Map<String, AttributeValue> reservation = dynamoDB.getItem(getRequest).getItem();
        if (reservation == null) {
            throw new RuntimeException("Reservation not found.");
        }

        String status = reservation.get("status").getS();

        if (status.equals(ReservationStatus.IN_PROGRESS.name()) || status.equals(ReservationStatus.FINISHED.name())) {
            if (!isValidRating(feedback.serviceRating())) {
                throw new RuntimeException("Invalid Service rating");
            }
            else {
                saveFeedback(reservation.get("feedbackId").getS(), "SERVICE", feedback.serviceRating(), feedback.serviceComment());
            }
            if (!isValidRating(feedback.cuisineRating()))
            {
                throw new RuntimeException("Invalid Cuisine rating");
            }
            else{
                saveFeedback(reservation.get("feedbackId").getS(), "CUISINE", feedback.cuisineRating(), feedback.cuisineComment());
            }
        }
        else
        {
            throw new RuntimeException("Feedback can be provided when reservation status is IN_PROGRESS or FINISHED");
        }
        // Step 4: Mark reservation as FINISHED
        Map<String, AttributeValueUpdate> updateStatus = new HashMap<>();
        updateStatus.put("status", new AttributeValueUpdate()
                .withValue(new AttributeValue().withS(ReservationStatus.FINISHED.name()))
                .withAction(AttributeAction.PUT));

        if(status.equals(ReservationStatus.IN_PROGRESS.name()))
            sendMessage(feedback.reservationId());

        dynamoDB.updateItem(new UpdateItemRequest()
                .withTableName(reservationsTable)
                .withKey(reservationKey)
                .withAttributeUpdates(updateStatus));

        return reservation.get("waiterEmail").getS();

    }

    public void sendMessage (String messageBody) {
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);
        sqsClient.sendMessage(sendMessageRequest);
    }

private void saveFeedback(String id, String type, String rating, String comments) {
    Map<String, AttributeValue> key = new HashMap<>();
    Map<String,AttributeValue> attributeMap= new HashMap<>();
    key.put("id", new AttributeValue().withS(id));
    key.put("type", new AttributeValue().withS(type));
    attributeMap.put(":rate", new AttributeValue().withS(rating));
    attributeMap.put(":comment", new AttributeValue().withS(comments));
    attributeMap.put(":date", new AttributeValue().withS(LocalDate.now().toString()));

    dynamoDB.updateItem(new UpdateItemRequest()
            .withTableName(feedbackTable)
            .withKey(key)
            .withUpdateExpression("SET rate = :rate, #ct= :comment, #dt = :date")
            .withExpressionAttributeValues(attributeMap)
            .withExpressionAttributeNames(Map.of("#dt","date","#ct","comment")));
}

    public boolean isValidRating(String ratingStr) {
            if (ratingStr == null) {
                return false;
            }
            try {
                double rating = Double.parseDouble(ratingStr);
                // Check value range
                if (rating <= 0 || rating > 5) return false;
                // Allow at most one decimal place
                String[] parts = ratingStr.split("\\.");
                return parts.length == 1 || parts[1].length() <= 1;
            } catch (NumberFormatException e) {
                return false;
            }
    }

    public String getServiceRatingById(String id) {
        Map<String, AttributeValue> attributeMap = Map.of("id", new AttributeValue().withS(id), "type", new AttributeValue().withS(FeedbackType.SERVICE.name()));

        return dynamoDB.getItem(
                new GetItemRequest()
                        .withTableName(feedbackTable)
                        .withKey(attributeMap)
        ).getItem().getOrDefault("rate", new AttributeValue().withS("-1")).getS();
    }

    public Feedback getFeedbackByReservationId(String reservationId) {

        Map<String, AttributeValue> reservationKey = Map.of("id", new AttributeValue().withS(reservationId));
        GetItemRequest getRequest = new GetItemRequest()
                .withTableName(reservationsTable)
                .withKey(reservationKey);
        Map<String, AttributeValue> reservation = dynamoDB.getItem(getRequest).getItem();

        if (reservation == null) {
            throw new RuntimeException("The reservation with given Id does not exist");
        }

        String feedbackId = reservation.get("feedbackId").getS();

        Map<String, AttributeValue> serviceFeedbackKey = Map.of(
                "id", new AttributeValue().withS(feedbackId),
                "type", new AttributeValue().withS(FeedbackType.SERVICE.name())
        );

        GetItemRequest getFeedbackRequest = new GetItemRequest()
                .withTableName(feedbackTable)
                .withKey(serviceFeedbackKey);

        Map<String, AttributeValue> serviceFeedback = dynamoDB.getItem(getFeedbackRequest).getItem();

        Map<String, AttributeValue> cuisineFeedbackKey = Map.of(
                "id", new AttributeValue().withS(feedbackId),
                "type", new AttributeValue().withS(FeedbackType.CUISINE.name())
        );

        GetItemRequest getCuisineFeedbackRequest = new GetItemRequest()
                .withTableName(feedbackTable)
                .withKey(cuisineFeedbackKey);

        Map<String, AttributeValue> cuisineFeedback = dynamoDB.getItem(getCuisineFeedbackRequest).getItem();

        if(serviceFeedback==null)
            serviceFeedback = new HashMap<>();
        if(cuisineFeedback==null)
            cuisineFeedback = new HashMap<>();

        String serviceRating = serviceFeedback.getOrDefault("rate", new AttributeValue().withS("")).getS();
        String serviceComment = serviceFeedback.getOrDefault("comment", new AttributeValue().withS("")).getS();
        String cuisineRating = cuisineFeedback.getOrDefault("rate", new AttributeValue().withS("")).getS();
        String cuisineComment = cuisineFeedback.getOrDefault("comment", new AttributeValue().withS("")).getS();

        return new Feedback(reservationId, serviceRating, serviceComment, cuisineRating, cuisineComment);
    }

}
