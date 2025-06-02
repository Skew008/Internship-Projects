package com.task11.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.JSONObject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.LinkedHashMap;
import java.util.Map;

public class GetTableByIdHandler extends CognitoSupport implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private final String tablesTable = System.getenv("tables_table");

    public GetTableByIdHandler(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        try {

            int tableId = Integer.parseInt(apiGatewayProxyRequestEvent.getPathParameters().get("tableId"));
            Map<String, AttributeValue> key = Map.of("id", new AttributeValue().withN(String.valueOf(tableId)));

            GetItemRequest request = new GetItemRequest().withTableName(tablesTable).withKey(key);
            Map<String, AttributeValue> result = client.getItem(request).getItem();

            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("id", Integer.parseInt(result.get("id").getN()));
            map.put("number", Integer.parseInt(result.get("number").getN()));
            map.put("places", Integer.parseInt((result.get("places").getN())));
            map.put("isVip", result.get("isVip").getBOOL());
            map.put("minOrder", Integer.parseInt(result.get("minOrder").getN()));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject(map).toString());
        }
        catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("There was an error in the request.");
        }
    }
}
