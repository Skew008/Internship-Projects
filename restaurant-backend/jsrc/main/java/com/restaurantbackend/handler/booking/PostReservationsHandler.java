package com.restaurantbackend.handler.booking;

import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.Enum.ReservationStatus;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class PostReservationsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationService reservationService;
    private final LocationService locationService; // Service to fetch location address
    private final CognitoSupport cognitoSupport;
    private final WaiterService waiterService;
    private final TablesService tablesService;
    public PostReservationsHandler(ReservationService reservationService, LocationService locationService, CognitoSupport cognitoSupport, WaiterService waiterService, TablesService tablesService) {
        this.reservationService = reservationService;
        this.locationService = locationService;
        this.cognitoSupport = cognitoSupport;
        this.waiterService = waiterService;
        this.tablesService = tablesService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            // Parse request body into DTO
            ReservationRequest reservation = ReservationRequest.fromJson(request.getBody());
            String email = cognitoSupport.getEmailByToken(request,context);

            tablesService.validTableDetails(reservation);
            List<String> allWaiterByLocationId=waiterService.getWaiterByLocation(reservation.getLocationId());
            if(allWaiterByLocationId==null)
            {
                throw new IllegalArgumentException("No waiters available for this location");
            }
            String allotedWaiter=reservationService.allotWaiter(reservation.getLocationId(),reservation.getDate(),reservation.getTimeFrom(),allWaiterByLocationId);
            // Create reservation and get ID
            Map<String, AttributeValue> confirmedReservation = reservationService.createReservation(reservation,email,allotedWaiter, false);

            // Fetch location address using locationId
            String locationAddress = locationService.getLocationAddress(reservation.getLocationId());


            // Construct response JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("id", confirmedReservation.get("id").getS());
            responseJson.put("status", ReservationStatus.RESERVED.name());
            responseJson.put("locationAddress", locationAddress);
            responseJson.put("date", reservation.getDate());
            responseJson.put("timeSlot", reservation.getTimeFrom() + " - " + reservation.getTimeTo());
            responseJson.put("preOrder", "0");
            responseJson.put("guestsNumber", reservation.getGuestsNumber());
            responseJson.put("feedbackId", confirmedReservation.get("feedbackId").getS());
            responseJson.put("tableNumber", confirmedReservation.get("tableNumber").getS());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(responseJson.toString());

        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody(new JSONObject().put("error", "Reservation failed").toString());
        }
    }
}
