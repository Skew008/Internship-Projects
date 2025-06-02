package com.restaurantbackend.handler.dish;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.DishService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class GetPopularDishesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DishService dishService;

    public GetPopularDishesHandler(DishService dishService) {
        this.dishService = dishService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            List<JSONObject> popularDishes = dishService.getPopularDishes();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(popularDishes.toString());
        } catch (Exception e) {
            context.getLogger().log(e.getMessage()+ "-------" + Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("message", e.getMessage()).toString());
        }
    }
}
