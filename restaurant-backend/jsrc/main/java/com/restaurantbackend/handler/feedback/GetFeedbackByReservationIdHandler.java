package com.restaurantbackend.handler.feedback;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.Feedback;
import com.restaurantbackend.service.FeedbackService;
import org.json.JSONObject;

import java.util.Map;

public class GetFeedbackByReservationIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final FeedbackService feedbackService;

    public GetFeedbackByReservationIdHandler(FeedbackService feedbackService) {

        this.feedbackService = feedbackService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            String reservationId = requestEvent.getPathParameters().get("id");

            if (reservationId == null || reservationId.isEmpty()) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(400)
                        .withBody(new JSONObject().put("message", "Reservation Id is required").toString());
            }

            Feedback feedback = feedbackService.getFeedbackByReservationId(reservationId);

            JSONObject feedbackInJson = new JSONObject();
            feedbackInJson.put("serviceRating", feedback.serviceRating());
            feedbackInJson.put("serviceComment", feedback.serviceComment());
            feedbackInJson.put("cuisineRating", feedback.cuisineRating());
            feedbackInJson.put("cuisineComment", feedback.cuisineComment());

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(feedbackInJson.toString());

        } catch(Exception e){

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", e.getMessage()).toString());
        }
    }
}
