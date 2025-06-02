package com.restaurantbackend.handler.location;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.DishService;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GetPopularDishesByLocationIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DishService dishService;

    public GetPopularDishesByLocationIdHandler(DishService dishService) {
        this.dishService = dishService;
    }
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            String locationId = requestEvent.getPathParameters().get("id");

            if (locationId == null) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withBody("{\"error\": \"Missing location ID\"}");
            }
            if(dishService.checkLocationExists(locationId)) {
               return new APIGatewayProxyResponseEvent()
                       .withStatusCode(400)
                       .withBody("IKnvalid location id provided");
            }
            context.getLogger().log("location id: " + locationId);
            List<JSONObject> specialityDishes = dishService.getSpecialityDishes(locationId);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(specialityDishes.toString());
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", "Error fetching speciality dishes").toString());
        }


    }
}
