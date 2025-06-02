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

import java.util.*;

public class GetTablesHandler extends CognitoSupport implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
    private final String tablesTable = System.getenv("tables_table");

    public GetTablesHandler(CognitoIdentityProviderClient cognitoClient) {
        super(cognitoClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        try {

            List<Map<String, AttributeValue>> collect = client.scan(new ScanRequest(tablesTable)).getItems();
            List<LinkedHashMap<String, Object>> res = new ArrayList<>();
            for(Map<String, AttributeValue> m:collect)
            {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("id", Integer.parseInt(m.get("id").getN()));
                map.put("number", Integer.parseInt(m.get("number").getN()));
                map.put("places", Integer.parseInt((m.get("places").getN())));
                map.put("isVip", m.get("isVip").getBOOL());
                map.put("minOrder", Integer.parseInt(m.get("minOrder").getN()));
                res.add(new LinkedHashMap<>(map));
            }
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("tables", res).toString());
        }
        catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("There was an error in the request");
        }
    }
}
