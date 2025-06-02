package com.restaurantbackend.handler.reservation;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.LocationService;
import com.restaurantbackend.service.ReservationService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class GetReservationByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationService reservationService;
    private final LocationService locationService;

    public GetReservationByIdHandler(ReservationService reservationService, LocationService locationService) {
        this.reservationService = reservationService;
        this.locationService = locationService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {

            Map<String, AttributeValue> reservation = reservationService.getReservationById(requestEvent.getPathParameters().get("id"));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("id", reservation.get("id").getS())
                            .put("locationId", reservation.get("locationId").getS())
                            .put("tableNumber", reservation.get("tableNumber").getS())
                            .put("date", reservation.get("date").getS())
                            .put("timeFrom", ReservationRequest.formatTimeToAmPm(reservation.get("timeFrom").getS()))
                            .put("timeTo", ReservationRequest.formatTimeToAmPm(reservation.get("timeTo").getS()))
                            .put("locationAddress", locationService.getLocationAddress(reservation.get("locationId").getS()))
                            .put("guestsNumber", reservation.get("guestsNumber").getS())
                            .toString());

        } catch (Exception e) {
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
