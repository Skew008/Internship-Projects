package com.restaurantbackend.handler.booking;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.Enum.ReservationStatus;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.*;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PostReservationByWaiterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ReservationService reservationService;
    private final WaiterService waiterService;
    private final CognitoSupport cognitoSupport;
    private final LocationService locationService;
    private final UserService userService;
    private final TablesService tablesService;

    public PostReservationByWaiterHandler(ReservationService reservationService, WaiterService waiterService, CognitoSupport cognitoSupport, LocationService locationService, UserService userService, TablesService tablesService) {
        this.reservationService = reservationService;
        this.waiterService = waiterService;
        this.cognitoSupport = cognitoSupport;
        this.locationService = locationService;
        this.userService = userService;
        this.tablesService = tablesService;
    }
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context)
    {
        try
        {

            ReservationRequest reservationRequestByWaiter=ReservationRequest.fromJson(requestEvent.getBody());
            tablesService.validTableDetails(reservationRequestByWaiter);
            String waiterEmail=cognitoSupport.getEmailByToken(requestEvent,context);
            List<String> alllWaitersByLocationId=waiterService.getWaiterByLocation(reservationRequestByWaiter.getLocationId());
            if(alllWaitersByLocationId==null)
            {
                throw new IllegalArgumentException("No waiter available for this location");
            }
            if(!alllWaitersByLocationId.contains(waiterEmail))
            {
                throw new IllegalArgumentException("waiter is not assigned for this restaurant location");
            }
            JSONObject obj=new JSONObject(requestEvent.getBody());
            String userEmail=obj.optString("customerEmail", waiterEmail);
            String clientType=obj.getString("clientType");
            if(!(clientType.equalsIgnoreCase("Customer") || clientType.equalsIgnoreCase("Visitor")))
                throw new IllegalArgumentException("Invalid clientType");
            Map<String,AttributeValue> userNameMap=userService.getUserByEmail(userEmail);
            if(userNameMap==null)
            {
                throw new IllegalArgumentException("Customer  with this email does not exist ");
            }

            Map<String, AttributeValue> confirmedReservation =reservationService.createReservation(reservationRequestByWaiter,userEmail,waiterEmail, true);

            String locationAddress = locationService.getLocationAddress(reservationRequestByWaiter.getLocationId());



            // Construct response JSON
            JSONObject responseJson = new JSONObject();
            responseJson.put("id", confirmedReservation.get("id").getS());
            responseJson.put("status", ReservationStatus.RESERVED.name());
            responseJson.put("locationAddress", locationAddress);
            responseJson.put("date", reservationRequestByWaiter.getDate());
            responseJson.put("timeSlot", reservationRequestByWaiter.getTimeFrom() + " - " + reservationRequestByWaiter.getTimeTo());
            responseJson.put("preOrder", "0");
            responseJson.put("guestsNumber", reservationRequestByWaiter.getGuestsNumber());
            responseJson.put("feedbackId", confirmedReservation.get("feedbackId").getS());
            if(clientType.equalsIgnoreCase("Customer"))
            {
                responseJson.put("userInfo","Customer "+userNameMap.get("firstName").getS()+" "+userNameMap.get("lastName").getS());
            }
            else
            {
                responseJson.put("userInfo","Waiter "+userNameMap.get("firstName").getS()+" "+userNameMap.get("lastName").getS()+" (For visitor)");
            }

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(responseJson.toString());
    } catch (Exception e) {
        context.getLogger().log("Error: " + e.getMessage());
        context.getLogger().log(Arrays.toString(e.getStackTrace()));
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(400)
                .withBody(new JSONObject().put("error", e.getMessage()).toString());
    }
    }
}
