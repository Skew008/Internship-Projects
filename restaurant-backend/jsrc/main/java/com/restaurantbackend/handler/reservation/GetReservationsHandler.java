package com.restaurantbackend.handler.reservation;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.CognitoSupport;
import com.restaurantbackend.service.LocationService;
import com.restaurantbackend.service.ReservationService;
import com.restaurantbackend.service.UserService;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GetReservationsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationService reservationService;
    private final CognitoSupport cognitoSupport;
    private final LocationService locationService;
    private final UserService userService;

    public GetReservationsHandler(ReservationService reservationService, CognitoSupport cognitoSupport, LocationService locationService, UserService userService) {
        this.reservationService = reservationService;
        this.cognitoSupport = cognitoSupport;
        this.locationService = locationService;
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String email = cognitoSupport.getEmailByToken(requestEvent, context);
        if(email==null || email.isEmpty()){
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", "email is required").toString());
        }
        try{
            reservationService.updateReservationStatuses(context);
            List<Map<String, AttributeValue>> reservations = reservationService.getReservations(email);
            Collection<JSONObject> list = reservations.stream()
                    .map(item -> new JSONObject()
                            .put("id", item.get("id").getS())
                            .put("status", item.get("status").getS())
                            .put("locationAddress", locationService.getLocationAddress(item.get("locationId").getS()))
                            .put("date", item.get("date").getS())
                            .put("timeSlot", ReservationRequest.formatTimeToAmPm(item.get("timeFrom").getS())+" - "+  ReservationRequest.formatTimeToAmPm(item.get("timeTo").getS()))
                            .put("preOrder", item.get("preOrder").getS())
                            .put("guestsNumber", item.get("guestsNumber").getS())
                            .put("feedbackId", item.get("feedbackId").getS())
                            .put("tableNumber", item.get("tableNumber").getS())
                            .put("waiter", userService.getUserByEmail(item.get("waiterEmail").getS()).get("firstName").getS()+" "+userService.getUserByEmail(item.get("waiterEmail").getS()).get("lastName").getS())
                    ).toList();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(list.toString());
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", "Reservation not found").toString());
        }
    }
}
