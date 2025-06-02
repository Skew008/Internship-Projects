package com.restaurantbackend.handler.location;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.Pageable;
import com.restaurantbackend.dto.Sort;
import com.restaurantbackend.service.FeedbackService;
import com.restaurantbackend.service.ReservationService;
import com.restaurantbackend.service.S3Service;
import com.restaurantbackend.service.UserService;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetFeedbacksByLocationIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReservationService reservationService;
    private final FeedbackService feedbackService;
    private final UserService userService;
    private final S3Service s3Service;

    public GetFeedbacksByLocationIdHandler(ReservationService reservationService, FeedbackService feedbackService, UserService userService, S3Service s3Service) {
        this.reservationService = reservationService;
        this.feedbackService = feedbackService;
        this.userService = userService;
        this.s3Service = s3Service;
    }



    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        try {

            if(requestEvent.getPathParameters()==null)
                throw new RuntimeException("Location Id is required");
            if(requestEvent.getQueryStringParameters()==null || !requestEvent.getQueryStringParameters().containsKey("type"))
                throw new RuntimeException("Type is required");

            String locationId = requestEvent.getPathParameters().get("id");
            String type = requestEvent.getQueryStringParameters().get("type");
            int page = Integer.parseInt(requestEvent.getQueryStringParameters().getOrDefault("page", "0"));
            int size = Integer.parseInt(requestEvent.getQueryStringParameters().getOrDefault("size", "4"));
            String inputSort = requestEvent.getMultiValueQueryStringParameters()==null ? "rate,desc" : requestEvent.getMultiValueQueryStringParameters().getOrDefault("sort", List.of("rate,desc")).get(0);

            if(page < 0)
                throw new IllegalArgumentException("Page Number cannot be negative");
            if(size < 0)
                throw new IllegalArgumentException("Size of page cannot be negative");

            List<Map<String, AttributeValue>> allFeedbacksByLocation = reservationService.getAllFeedbacksByLocation(locationId);

            List<JSONObject> contents = new ArrayList<>();
            for(Map<String, AttributeValue> m: allFeedbacksByLocation)
            {
                String email = m.get("email").getS();
                String feedbackId = m.get("feedbackId").getS();

                Map<String, AttributeValue> user = userService.getUserByEmail(email);

                context.getLogger().log(m.toString());

                JSONObject feedbacksContent = feedbackService
                        .getFeedbacksContent(locationId, type,
                                user.get("firstName").getS() + " " + user.get("lastName").getS(),
                                s3Service.getBase64ImageFromS3(user.get("imageUrl").getS()), feedbackId);
                if(feedbacksContent.get("rate").toString().isEmpty())
                    continue;
                contents.add(feedbacksContent);
            }
            contents = feedbackService.getSortedFeedbacks(contents, inputSort);
            int totalElements = contents.size();
            int totalPages = (int) Math.ceil(totalElements*1.0/size);
            int numberOfElements = totalElements < size*(page+1) ? totalElements % size : size;
            if(page>=totalPages)
                throw new IllegalArgumentException("Page Number Invalid");

            JSONObject sort = Sort.toJson(feedbackService.getSort(inputSort));
            JSONObject pagable = Pageable.toJson(feedbackService.getPagable(page, inputSort, size));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject()
                            .put("totalPages", totalPages)
                            .put("totalElements", totalElements)
                            .put("size", size)
                            .put("content", contents.stream()
                                    .skip(size*page)
                                    .limit(numberOfElements)
                                    .toList())
                            .put("number", page)
                            .put("sort", sort)
                            .put("first", page==0)
                            .put("last", page==totalPages-1)
                            .put("numberOfElements", numberOfElements)
                            .put("pageable", pagable)
                            .put("empty", true)
                            .toString());

        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            context.getLogger().log(Arrays.toString(e.getStackTrace()));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("Error", "Feedbacks cannot be fetched").toString());
        }
    }
}
