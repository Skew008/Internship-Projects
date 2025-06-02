package com.restaurantbackend.handler.feedback;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.restaurantbackend.service.S3Service;
import com.restaurantbackend.service.UserService;
import com.restaurantbackend.service.WaiterService;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;


public class GetWaiterDetailsByReservationId implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final WaiterService waiterService;
    private final UserService userService;
    private final S3Service s3Service;
    public GetWaiterDetailsByReservationId(WaiterService waiterService, UserService userService, S3Service s3Service) {
        this.waiterService = waiterService;
        this.userService = userService;
        this.s3Service = s3Service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
   String reservationId= requestEvent.getQueryStringParameters().get("reservationId");
        try
        {
            Map<String, AttributeValue> waiterDetailsByReservationId = waiterService.getWaiterDetailsByReservationId(reservationId);
            Map<String, AttributeValue> waiter = userService.getUserByEmail(waiterDetailsByReservationId.get("email").getS());
            JSONObject jsonResponse=new JSONObject();
            jsonResponse.put("waiterName", waiter.get("firstName").getS()+" "+ waiter.get("lastName").getS());
            jsonResponse.put("waiterImage", s3Service.getBase64ImageFromS3(waiter.get("imageUrl").getS()));
         double rating=   Double.parseDouble(waiterDetailsByReservationId.get("totalRating").getN())/Double.parseDouble(waiterDetailsByReservationId.get("orderProcessed").getN());


           jsonResponse.put("rating",String.valueOf(new DecimalFormat("#.00").format(rating)));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(jsonResponse.toString());
        }
        catch (Exception e)
        {
           context.getLogger().log(e.getMessage());
           context.getLogger().log(Arrays.toString(e.getStackTrace()));
           return new APIGatewayProxyResponseEvent()
                   .withStatusCode(400)
                   .withBody(new JSONObject().put("error",e.getMessage()).toString());
        }
    }
}
