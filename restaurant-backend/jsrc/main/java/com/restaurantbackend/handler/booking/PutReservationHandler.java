package com.restaurantbackend.handler.booking;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.Enum.ReservationStatus;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.LocationService;
import com.restaurantbackend.service.ReservationService;
import com.restaurantbackend.service.TablesService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class PutReservationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final TablesService tablesService;
    private final ReservationService reservationService;
    private final LocationService locationService;

    public PutReservationHandler(TablesService tablesService, ReservationService reservationService, LocationService locationService) {
        this.tablesService = tablesService;
        this.reservationService = reservationService;
        this.locationService = locationService;
    }


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {

        try {

            ReservationRequest reservation = ReservationRequest.fromJson(request.getBody());

            tablesService.validTableDetails(reservation);

            // Create reservation and get ID
            Map<String, AttributeValue> confirmedReservation = reservationService.updateReservation(reservation);

            // Fetch location address using locationId
            String locationAddress = locationService.getLocationAddress(reservation.getLocationId());


            // Construct response JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("id", confirmedReservation.get("id").getS());
            responseJson.put("status", ReservationStatus.RESERVED.name());
            responseJson.put("locationAddress", locationAddress);
            responseJson.put("date", reservation.getDate());
            responseJson.put("timeSlot", reservation.getTimeFrom() + " - " + reservation.getTimeTo());
            responseJson.put("preOrder", confirmedReservation.get("preOrder").getS());
            responseJson.put("guestsNumber", reservation.getGuestsNumber());
            responseJson.put("feedbackId", confirmedReservation.get("feedbackId").getS());
            responseJson.put("tableNumber", confirmedReservation.get("tableNumber").getS());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(responseJson.toString());

        } catch (Exception e) {
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("error", e.getMessage()).toString());
        }
    }
}
