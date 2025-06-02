package com.restaurantbackend.handler.cart;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

    public class GetCartOfParticularReservationId implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationService reservationService;
    private final OrderService orderService;
    private final DishService dishService;
    private final LocationService locationService;

    public GetCartOfParticularReservationId(ReservationService reservationService, OrderService orderService, DishService dishService, LocationService locationService) {

        this.reservationService = reservationService;
        this.orderService = orderService;
        this.dishService = dishService;
        this.locationService = locationService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {
            Map<String, String> pathParameters = requestEvent.getPathParameters();
            String reservationId;
            if (pathParameters != null && pathParameters.containsKey("reservationId")) {
                reservationId = pathParameters.get("reservationId");
            } else {
                throw new IllegalArgumentException("No reservation Id exist");
            }
            Map<String, AttributeValue> reservation = reservationService.getReservationById(reservationId);

            JSONObject response = new JSONObject();
            response.put("address", locationService.getLocationAddress(reservation.get("locationId").getS()));
            response.put("date", reservation.get("date").getS());
            response.put("dishItems", dishService.getDishItems(orderService.getOrderDishes(reservation.get("orderId").getS())));
            response.put("id", reservation.get("orderId").getS());
            response.put("reservationId", reservation.get("id").getS());
            response.put("state", Integer.parseInt(reservation.get("preOrder").getS()) != 0 ? "SUBMITTED" : "IN PROCESS");
            response.put("timeSlot", ReservationRequest.formatTimeToAmPm(reservation.get("timeFrom").getS()) + " - " + ReservationRequest.formatTimeToAmPm(reservation.get("timeTo").getS()));


            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("contents", response).toString());

        } catch (Exception exception) {

            context.getLogger().log(exception.getMessage());
            context.getLogger().log(Arrays.toString(exception.getStackTrace()));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Cart not available");
        }

    }
}
