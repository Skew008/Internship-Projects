package com.restaurantbackend.handler.dish;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.DishService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class GetDishByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DishService dishService;

    public GetDishByIdHandler(DishService dishService) {
        this.dishService = dishService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            String dishId = requestEvent.getPathParameters().get("id");
            Map<String, AttributeValue> dish = dishService.getDishById(dishId);

            if(dish == null) throw new RuntimeException("There is no dish with the corresponding id");



            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("carbohydrates", dish.get("carbohydrates").getS())
                            .put("weight", dish.get("weight").getS())
                            .put("description", dish.get("description").getS())
                            .put("calories", dish.get("calories").getS())
                            .put("vitamins", dish.get("vitamins").getS())
                            .put("fats", dish.get("fats").getS())
                            .put("dishType", dish.get("dishType").getS())
                            .put("proteins", dish.get("proteins").getS())
                            .put("price", dish.get("price").getS())
                            .put("imageUrl", dish.get("imageUrl").getS())
                            .put("name", dish.get("name").getS())
                            .put("state", dishService.getDishStateFromOverallStock(dishId))
                            .put("id", dish.get("id").getS())
                            .toString());

        } catch (Exception e) {
            context.getLogger().log(e.getMessage()+ "-------" + Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(e.getMessage());
        }
    }
}

