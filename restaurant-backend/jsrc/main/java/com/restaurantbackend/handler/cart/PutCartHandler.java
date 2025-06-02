package com.restaurantbackend.handler.cart;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.Order;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.*;
import org.checkerframework.checker.units.qual.A;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class PutCartHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoSupport cognitoSupport;
    private final ReservationService reservationService;
    private final OrderService orderService;
    private final DishService dishService;

    public PutCartHandler(CognitoSupport cognitoSupport, ReservationService reservationService, OrderService orderService, DishService dishService) {
        this.cognitoSupport = cognitoSupport;
        this.reservationService = reservationService;
        this.orderService = orderService;
        this.dishService = dishService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {

            Order order = Order.fromJSON(requestEvent.getBody());

            Map<String, AttributeValue> reservation = reservationService.getReservationById(order.reservationId());
            reservationService.updateReservationStatusById(reservation.get("id").getS());
            try {
                if(!reservationService.checkReservationValidForCancellation(reservation))
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Set<String> dishIds = orderService.getOrderDishes(order.id()).stream().map(item -> item.get("dishId").getS()).collect(Collectors.toSet());

            int preOrder = orderService.placeOrder(order.id(), order.dishItems(), dishIds);

            reservationService.updatePreOrder(preOrder, order.reservationId());

            String email = cognitoSupport.getEmailByToken(requestEvent,context);

            List<Map<String, AttributeValue>> reservationsByEmail = reservationService.getReservationByEmail(email);
            List<JSONObject> contents = new ArrayList<>();

            for(Map<String, AttributeValue> m: reservationsByEmail)
            {
                JSONObject response = new JSONObject();
                response.put("address", order.address());
                response.put("date",order.date());
                response.put("dishItems", dishService.getDishItems(orderService.getOrderDishes(m.get("orderId").getS())));
                response.put("id",order.id());
                response.put("reservationId",order.reservationId());
                response.put("state",preOrder!=0 ? "SUBMITTED" : "CANCELLED");
                response.put("timeSlot", order.timeSlot());
                contents.add(response);
            }


            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("content", contents).toString());

        } catch (Exception exception) {

            context.getLogger().log(exception.getMessage());
            context.getLogger().log(Arrays.toString(exception.getStackTrace()));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody("Error processing order, try again");
        }
    }
}
