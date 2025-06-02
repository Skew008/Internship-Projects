package com.restaurantbackend.handler.reservation;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.Enum.ReservationStatus;
import com.restaurantbackend.service.CognitoSupport;
import com.restaurantbackend.service.DishService;
import com.restaurantbackend.service.ReservationService;
import com.restaurantbackend.service.UserService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAvailableDishesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DishService dishService;
    private final ReservationService reservationService;
    private final CognitoSupport cognitoSupport;
    private final UserService userService;

    public GetAvailableDishesHandler(DishService dishService, ReservationService reservationService, CognitoSupport cognitoSupport, UserService userService) {
        this.dishService = dishService;
        this.reservationService = reservationService;
        this.cognitoSupport = cognitoSupport;
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try{

            Map<String, String> queryStringParameters = requestEvent.getQueryStringParameters()==null ? new HashMap<>() : requestEvent.getQueryStringParameters();

            context.getLogger().log(requestEvent.getPathParameters().toString());
            Map<String, AttributeValue> reservation = reservationService.getReservationById(requestEvent.getPathParameters().get("id"));
            reservationService.updateReservationStatusById(reservation.get("id").getS());
            String role = userService.getUserByEmail(cognitoSupport.getEmailByToken(requestEvent,context)).get("role").getS();
            try {
                if(role.equalsIgnoreCase("customer") && !reservationService.checkReservationValidForCancellation(reservation))
                    throw new IllegalArgumentException("Cannot preorder past");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            reservationService.putOrderId(reservation.get("id").getS());

            String dishType = queryStringParameters.getOrDefault("dishType", "");
            if(!dishType.isEmpty()) {
                dishService.checkDishType(dishType);
            }
            String[] sortTypes = queryStringParameters.getOrDefault("sort", "popularity,desc").split(",");

            if(sortTypes.length > 2) throw new IllegalArgumentException("Invalid sort query provided");
            String sortType = sortTypes[0];

            String sortOrder = sortTypes[1];
            context.getLogger().log(sortOrder + "-----" + sortType);

            String location = reservation.get("locationId").getS();

            List<JSONObject> dishesAtALocation = dishService.getDishesAtALocation(location, dishType, sortType, sortOrder, context);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("content",dishesAtALocation).toString());
        } catch (RuntimeException e) {
            context.getLogger().log(e.getMessage()+ "-------" + Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("error","Not dishes available at this moment").toString());
        }
    }
}
