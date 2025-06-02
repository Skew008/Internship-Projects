package com.restaurantbackend.handler.reservation;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.ReservationRequest;
import com.restaurantbackend.service.*;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class GetReservationOfWaiterHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ReservationService reservationService;
    private final WaiterService waiterService;
    private final CognitoSupport cognitoSupport;
    private final LocationService locationService;
    private final UserService userService;
    public GetReservationOfWaiterHandler(ReservationService reservationService, WaiterService waiterService, CognitoSupport cognitoSupport, LocationService locationService, UserService userService) {
        this.reservationService = reservationService;

        this.waiterService = waiterService;
        this.cognitoSupport = cognitoSupport;
        this.locationService = locationService;
        this.userService = userService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String waiterEmail = cognitoSupport.getEmailByToken(requestEvent, context);

        try
        {
            List<String> waiters=waiterService.getWaiters();
            if(!waiters.contains(waiterEmail))
            {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withBody(new JSONObject().put("ErrorMessage","The logged in user is not a waiter").toString());

            }
           Map<String,String> queryStringParameters= requestEvent.getQueryStringParameters()==null ?  new HashMap<>() : requestEvent.getQueryStringParameters();


            List<Map<String, AttributeValue>> waiterReservation= reservationService.getReservationsOfWaiter(waiterEmail,queryStringParameters.getOrDefault("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date())), queryStringParameters.getOrDefault("timeFrom", "Any time"), queryStringParameters.getOrDefault("tableNumber", "Any table"),context);
            Collection<JSONObject> list=
                    waiterReservation
                            .stream()
                            .map(item->new JSONObject()
                                    .put("locationAddress",locationService.getLocationAddress(item.get("locationId").getS()))
                                    .put("date",item.get("date").getS())
                                    .put("timeSlot",ReservationRequest.formatTimeToAmPm(item.get("timeFrom").getS())+" - "+ReservationRequest.formatTimeToAmPm(item.get("timeTo").getS()))
                                    .put("preOrder",item.get("preOrder").getS())
                                    .put("tableNumber", item.get("tableNumber").getS())
                                    .put("guestsNumber",item.get("guestsNumber").getS())
                                    .put("reservationId", item.get("id").getS())
                                    .put("status", item.get("status").getS())
                                    .put("customerName", "Customer "+userService.getUserByEmail(item.get("email").getS()).get("firstName").getS()+" "+userService.getUserByEmail(item.get("email").getS()).get("lastName").getS())
                                    .put("waiterName", "Waiter "+userService.getUserByEmail(waiterEmail).get("firstName").getS()+" "+userService.getUserByEmail(waiterEmail).get("lastName").getS())
                                    .put("userInfo", !item.get("byWaiter").getBOOL() ?
                                            "Customer "+userService.getUserByEmail(item.get("email").getS()).get("firstName").getS()+" "+userService.getUserByEmail(item.get("email").getS()).get("lastName").getS()
                                            : item.get("email").getS().equalsIgnoreCase(waiterEmail) ?
                                            "Waiter "+userService.getUserByEmail(waiterEmail).get("firstName").getS()+" "+userService.getUserByEmail(waiterEmail).get("lastName").getS()+ " (For visitor)"
                                            : "Waiter "+userService.getUserByEmail(waiterEmail).get("firstName").getS()+" "+userService.getUserByEmail(waiterEmail).get("lastName").getS()+"( Customer "+userService.getUserByEmail(item.get("email").getS()).get("firstName").getS()+" "+userService.getUserByEmail(item.get("email").getS()).get("lastName").getS()+" )")

                            )
                            .toList();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(list.toString());

        }
        catch (Exception e)
        {
            context.getLogger().log(e.getMessage());
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("error message","Some error in getting reservations").toString());
        }
    }
}
