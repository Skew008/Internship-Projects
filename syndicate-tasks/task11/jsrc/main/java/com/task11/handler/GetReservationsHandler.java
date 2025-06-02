package com.task11.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.JSONObject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetReservationsHandler extends CognitoSupport implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private final String reservationsTable = System.getenv("reservations_table");

    public GetReservationsHandler(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        try {

            List<Map<String, AttributeValue>> collect = client.scan(new ScanRequest(reservationsTable)).getItems();
            List<LinkedHashMap<String, Object>> res = new ArrayList<>();
            for(Map<String, AttributeValue> m:collect)
            {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("tableNumber", Integer.parseInt(m.get("id").getN()));
                map.put("clientName", m.get("clientName").getS());
                map.put("phoneNumber", m.get("phoneNumber").getS());
                map.put("date", m.get("date").getS());
                map.put("slotTimeStart", m.get("slotTimeStart").getS());
                map.put("slotTimeEnd", m.get("slotTimeEnd").getS());
                res.add(new LinkedHashMap<>(map));
            }
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("reservations", res).toString());
        }
        catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("There was an error in the request");
        }
    }
}
