package com.restaurantbackend.handler.location;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.LocationService;
import org.json.JSONObject;

import java.util.List;

public class GetAllLocationsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final LocationService locationService;

    public GetAllLocationsHandler(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            List<JSONObject> allLocations = locationService.getAllLocations();

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(allLocations.toString());
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", "Location not found").toString());
        }
    }
}
