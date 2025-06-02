package com.task11.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.task11.dto.Reservations;
import org.json.JSONObject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.*;

public class PostReservationsHandler extends CognitoSupport implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private final String tablesTable = System.getenv("tables_table");
    private final String reservationsTable = System.getenv("reservations_table");

    public PostReservationsHandler(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        try{

            Reservations r = Reservations.fromJson(apiGatewayProxyRequestEvent.getBody());

            if (!isTableExists(r.getTableNumber())) {
                throw new Exception();
            }
            if (!isTimeSlotAvailable(r.getTableNumber(), r.getDate(), r.getSlotTimeStart(), r.getSlotTimeEnd())) {
                throw new Exception();
            }

            Map<String, AttributeValue> reservation = new LinkedHashMap<>();
            reservation.put("id", new AttributeValue().withN(String.valueOf(r.getTableNumber())));
            reservation.put("clientName", new AttributeValue().withS(r.getClientName()));
            reservation.put("phoneNumber", new AttributeValue().withS(r.getPhoneNumber()));
            reservation.put("date", new AttributeValue().withS(r.getDate()));
            reservation.put("slotTimeStart", new AttributeValue().withS(r.getSlotTimeStart()));
            reservation.put("slotTimeEnd", new AttributeValue().withS(r.getSlotTimeEnd()));

            PutItemRequest putItemRequest = new PutItemRequest()
                    .withTableName(reservationsTable)
                    .withItem(reservation);

            client.putItem(putItemRequest);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("reservationId", UUID.randomUUID()).toString());
        }
        catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("There was an error in the request");
        }
    }

    private boolean isTableExists(int tableNumber) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(tablesTable)
                .withFilterExpression("#num = :tableNumber")
                .withExpressionAttributeNames(Collections.singletonMap("#num", "number"))
                .withExpressionAttributeValues(Collections.singletonMap(":tableNumber", new AttributeValue().withN(String.valueOf(tableNumber))));

        ScanResult result = client.scan(scanRequest);
        return !result.getItems().isEmpty();
    }

    private boolean isTimeSlotAvailable(int tableNumber, String date, String startTime, String endTime) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(reservationsTable)
                .withFilterExpression("id = :tableNumber AND #dt = :date")
                .withExpressionAttributeNames(Collections.singletonMap("#dt", "date"))
                .withExpressionAttributeValues(Map.of(
                        ":tableNumber", new AttributeValue().withN(String.valueOf(tableNumber)),
                        ":date", new AttributeValue().withS(date)));

        ScanResult scanResult = client.scan(scanRequest);

        for (Map<String, AttributeValue> item : scanResult.getItems()) {
            String existingStart = item.get("slotTimeStart").getS();
            String existingEnd = item.get("slotTimeEnd").getS();
            if (isOverlapping(existingStart, existingEnd, startTime, endTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean isOverlapping(String existingStart, String existingEnd, String newStart, String newEnd) {
        return !(newEnd.compareTo(existingStart) <= 0 || newStart.compareTo(existingEnd) >= 0);
    }
}
