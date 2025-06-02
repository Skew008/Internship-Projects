package com.restaurantbackend.handler.booking;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.service.LocationService;
import com.restaurantbackend.service.ReservationService;
import com.restaurantbackend.service.TablesService;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class GetTablesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final TablesService tablesService;
    private final ReservationService reservationService;
    private final LocationService locationService;

    public GetTablesHandler(TablesService tablesService, ReservationService reservationService, LocationService locationService) {
        this.tablesService = tablesService;
        this.reservationService = reservationService;
        this.locationService = locationService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            Map<String, String> queryStringParameters = requestEvent.getQueryStringParameters()==null ? new HashMap<>() : requestEvent.getQueryStringParameters();
            reservationService.updateReservationStatuses(context);

            if(LocalDate.parse(queryStringParameters.getOrDefault("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))).isBefore(LocalDate.now()))
                throw new IllegalArgumentException("Can't do reservation in Past");

            String locationId = queryStringParameters.getOrDefault("locationId", null);
            if(locationId==null)
            {}
            else if(locationService.getLocationAddress(locationId).equals("Unknown Location"))
                throw new IllegalArgumentException();


            List<Map<String, AttributeValue>> allTablesByLocationAndCapacity =
                    tablesService.getAllTablesByLocationAndCapacity(
                            queryStringParameters.getOrDefault("locationId", null),
                            queryStringParameters.getOrDefault("guests", "0")
                    );

            List<JSONObject> resultTable = new ArrayList<>();

            for (var item : allTablesByLocationAndCapacity) {
                List<String> slots = tablesService.getAllSlots(queryStringParameters.getOrDefault("time", "00:00 AM"));
                List<String> notAvailableSlots = tablesService.getNotAvailableSlots(
                        item.get("tableNumber").getS(),
                        item.get("locationId").getS(),
                        queryStringParameters.getOrDefault("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                );
                slots.removeAll(notAvailableSlots);

                tablesService.removePastTimeSlots(slots, queryStringParameters.getOrDefault("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date())));

                resultTable.add(new JSONObject()
                        .put("locationId", item.get("locationId").getS())
                        .put("locationAddress", item.get("locationAddress").getS())
                        .put("tableNumber", item.get("tableNumber").getS())
                        .put("capacity", item.get("capacity").getS())
                        .put("availableSlots", tablesService.getFormattedSlots(slots))
                );
            }

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(resultTable.toString());

        } catch (Exception e) {
            context.getLogger().log(e.getMessage());
            context.getLogger().log(Arrays.toString(e.getStackTrace()));

            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(404)
                    .withBody(new JSONObject().put("ErrorMessage", "Tables not found").toString());
        }
    }
}
