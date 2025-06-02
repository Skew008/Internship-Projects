package com.restaurantbackend.handler.reports;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.restaurantbackend.dto.ReportContent;
import com.restaurantbackend.service.ReportService;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetReportsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ReportService reportService;

    public GetReportsHandler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try{
            Map<String, String> queryStringParameters = requestEvent.getQueryStringParameters()==null?new HashMap<>():requestEvent.getQueryStringParameters();
            List<ReportContent> reportContents = reportService.mainReportGetApi(queryStringParameters.get("type"),queryStringParameters.get("locationId"), queryStringParameters.get("waiterEmail"), queryStringParameters.get("fromDate"), queryStringParameters.get("toDate"));
            List<JSONObject> list = reportContents.stream().map(e -> ReportContent.toJson(e)).toList();
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("contents", list).toString());
        }catch(Exception e){
            context.getLogger().log(e.getMessage());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("ErrorMessage", "Reports not found").toString());
        }
    }
}
