package com.restaurantbackend.handler.location;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.LocationService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class GetListLocationsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final LocationService locationService;

    public GetListLocationsHandler(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try{
            List<JSONObject> allLocationsBasedOnSelectOptions = locationService.getAllLocationsBasedOnSelectOptions();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(allLocationsBasedOnSelectOptions.toString());
        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("errorMessage" , "Location not found").toString());
        }
    }
}
