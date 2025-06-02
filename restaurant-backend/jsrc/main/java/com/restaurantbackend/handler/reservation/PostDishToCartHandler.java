package com.restaurantbackend.handler.reservation;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.DishService;
import com.restaurantbackend.service.OrderService;
import com.restaurantbackend.service.ReservationService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class PostDishToCartHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationService reservationService;
    private final DishService dishService;
    private final OrderService orderService;

    public PostDishToCartHandler(ReservationService reservationService, DishService dishService, OrderService orderService) {
        this.reservationService = reservationService;
        this.dishService = dishService;
        this.orderService = orderService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {

            Map<String, AttributeValue> reservation = reservationService.getReservationById(requestEvent.getPathParameters().get("id"));
            reservationService.updateReservationStatusById(reservation.get("id").getS());
            try {
                if(!reservationService.checkReservationValidForCancellation(reservation))
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            String orderId = reservation.get("orderId").getS();
            String dishId = dishService.getDishById(requestEvent.getPathParameters().get("dishId")).get("id").getS();

            orderService.addOrderToCart(orderId, dishId);

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("message", "Dish has been placed to cart").toString());
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("message", "Dish Not added or already present").toString());
        }

    }
}
