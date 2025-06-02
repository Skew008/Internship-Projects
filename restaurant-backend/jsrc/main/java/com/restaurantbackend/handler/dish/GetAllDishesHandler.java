package com.restaurantbackend.handler.dish;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.DishService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GetAllDishesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DishService dishService;
    public GetAllDishesHandler(DishService dishService) {
        this.dishService = dishService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try{
            Map<String, String> queryStringParameters = requestEvent.getQueryStringParameters();
            String dishType = queryStringParameters.getOrDefault("dishType", "");
            if(!dishType.isEmpty()) {
                dishService.checkDishType(dishType);
            }
            String[] sortTypes = queryStringParameters.getOrDefault("sort", "popularity,desc").split(",");

            if(sortTypes.length > 2) throw new IllegalArgumentException("Invalid sort query provided");
            String sortType = sortTypes[0];

            String sortOrder = sortTypes[1];
//            if(!sortOrder.isEmpty() || !sortOrder.equals("asc") || !sortOrder.equals("desc")) throw new IllegalArgumentException("Not a proper sort order");
            context.getLogger().log(sortOrder + "-----" + sortType);
            List<JSONObject> dishes = dishService.getAllDishesByTypeAndSort(dishType, sortType, sortOrder, context);
            context.getLogger().log("dishes -----" + dishes);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("content", dishes).toString());
        } catch (RuntimeException e) {
            context.getLogger().log(e.getMessage()+ "-------" + Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(e.getMessage());
        }
    }
}
