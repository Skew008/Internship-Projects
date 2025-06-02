package com.restaurantbackend.handler.feedback;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.Feedback;
import com.restaurantbackend.service.FeedbackService;
import com.restaurantbackend.service.ReservationService;
import com.restaurantbackend.service.WaiterService;
import org.json.JSONObject;

import java.util.Arrays;

public class PostFeedbackHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final FeedbackService feedbackService;
    private final WaiterService waiterService;
    private final ReservationService reservationService;

    public PostFeedbackHandler(FeedbackService feedbackService, WaiterService waiterService, ReservationService reservationService) {
        this.feedbackService = feedbackService;
        this.waiterService = waiterService;
        this.reservationService = reservationService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try{
            Feedback request =Feedback.fromJson(requestEvent.getBody());
            String rating = feedbackService.getServiceRatingById(reservationService.getReservationById(request.reservationId()).get("feedbackId").getS());
            String waiterEmail = feedbackService.postFeedback(request);
            waiterService.updateRating(waiterEmail, request.serviceRating(), rating);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(201)
                    .withBody(new JSONObject().put("message", "Feedback has been created or updated").toString());
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", e.getMessage()).toString());
        }
    }
}
