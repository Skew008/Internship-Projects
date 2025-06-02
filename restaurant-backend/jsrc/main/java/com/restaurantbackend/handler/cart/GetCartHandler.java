package com.restaurantbackend.handler.cart;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.*;
import org.json.JSONObject;

import java.util.*;

public class GetCartHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoSupport cognitoSupport;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final DishService dishService;
    private final LocationService locationService;

    public GetCartHandler(CognitoSupport cognitoSupport, ReservationService reservationService, OrderService orderService, DishService dishService, LocationService locationService) {
        this.cognitoSupport = cognitoSupport;
        this.reservationService = reservationService;
        this.orderService = orderService;
        this.dishService = dishService;
        this.locationService = locationService;
    }


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {

            String email = cognitoSupport.getEmailByToken(requestEvent,context);

            List<Map<String, AttributeValue>> reservationsByEmail = reservationService.getReservationByEmail(email);
            List<JSONObject> contents = new ArrayList<>();

            for(Map<String, AttributeValue> m: reservationsByEmail)
            {
                JSONObject response = new JSONObject();
                response.put("address",locationService.getLocationAddress(m.get("locationId").getS()));
                response.put("date",m.get("date").getS());
                response.put("dishItems", dishService.getDishItems(orderService.getOrderDishes(m.get("orderId").getS())));
                response.put("id",m.get("orderId").getS());
                response.put("reservationId",m.get("id").getS());
                response.put("state",Integer.parseInt(m.get("preOrder").getS())!=0 ? "SUBMITTED" : "IN PROCESS");
                response.put("timeSlot", ReservationRequest.formatTimeToAmPm(m.get("timeFrom").getS()) +" - "+ReservationRequest.formatTimeToAmPm(m.get("timeTo").getS()));
                contents.add(response);
            }


            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("contents", contents).toString());

        } catch (Exception exception) {

            context.getLogger().log(exception.getMessage());
            context.getLogger().log(Arrays.toString(exception.getStackTrace()));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Cart not available");
        }

    }
}