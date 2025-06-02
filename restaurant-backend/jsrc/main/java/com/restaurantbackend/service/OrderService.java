package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderService {

    private final AmazonDynamoDB dynamoDB;
    private final String orderTable;

    public OrderService(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        this.orderTable = System.getenv("ORDERS_TABLE");
    }

    public void addOrderToCart(String orderId, String dishId) {

        GetItemResult order = dynamoDB.getItem(
                new GetItemRequest()
                        .withKey(Map.of("id", new AttributeValue().withS(orderId), "dishId", new AttributeValue().withS(dishId)))
                        .withTableName(orderTable)
        );

        if(order.getItem()==null)
        {
            dynamoDB.putItem(
                    new PutItemRequest()
                            .withTableName(orderTable)
                            .withItem(Map.of("id", new AttributeValue().withS(orderId), "dishId", new AttributeValue().withS(dishId), "quantity", new AttributeValue().withN("1")))
            );
        }
        else
            throw new IllegalArgumentException();
    }

    public List<Map<String, AttributeValue>> getOrderDishes(String id) {

        return dynamoDB.query(
                new QueryRequest()
                        .withTableName(orderTable)
                        .withKeyConditionExpression("id = :id")
                        .withExpressionAttributeValues(Map.of(":id", new AttributeValue().withS(id)))
        ).getItems();
    }

    public int placeOrder(String id, JSONArray dishItems, Set<String> dishIds) {

        int preOrderCount = 0;
        for(int i=0; i<dishItems.length(); i++)
        {
            JSONObject dish = dishItems.getJSONObject(i);
            dishIds.remove(dish.getString("dishId"));
            Map<String, AttributeValue> key = Map.of(
                    "id", new AttributeValue().withS(id),
                    "dishId", new AttributeValue().withS(dish.getString("dishId")));
            int quantity = dish.optInt("dishQuantity", 0);
//            if(quantity==0)
//            {
//                dynamoDB.deleteItem(
//                        new DeleteItemRequest()
//                                .withKey(key)
//                                .withTableName(orderTable)
//                );
//            }

//            else
//            {
                dynamoDB.updateItem(
                        new UpdateItemRequest()
                                .withTableName(orderTable)
                                .withKey(key)
                                .withUpdateExpression("SET quantity = :q")
                                .withExpressionAttributeValues(Collections.singletonMap(":q", new AttributeValue().withN(String.valueOf(quantity))))
                );
                preOrderCount += quantity;
//            }

        }
        for(String dishId: dishIds)
        {
            Map<String, AttributeValue> key;
            key = Map.of(
                    "id", new AttributeValue().withS(id),
                    "dishId", new AttributeValue().withS(dishId));

            dynamoDB.deleteItem(
                        new DeleteItemRequest()
                                .withKey(key)
                                .withTableName(orderTable)
                );
        }
        return preOrderCount;
    }

}
