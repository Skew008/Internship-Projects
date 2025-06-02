package com.restaurantbackend.handler.reservation;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.ReservationService;
import org.json.JSONObject;

public class DeleteReservationByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationService reservationService;

    public DeleteReservationByIdHandler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        String id = requestEvent.getPathParameters().get("id");
        if (id == null || id.isEmpty()) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", "Reservation ID is required").toString());
        }
        try{
            reservationService.deleteReservationbyId(id);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200);
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", e.getMessage()).toString());
        }

    }
}
