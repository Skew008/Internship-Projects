package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.restaurantbackend.Enum.DishType;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class DishService {

    private final AmazonDynamoDB dynamoDB;
    private final String dishesTable;
    private final String salesTable;
    private final String specialityDishesTable;

    public DishService(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        this.salesTable = System.getenv("SALES_TABLE");
        this.dishesTable = System.getenv("DISHES_TABLE");
        this.specialityDishesTable = System.getenv("SPECIALITY_DISHES_TABLE");
    }

    public List<JSONObject> getPopularDishes () {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(salesTable);
        ScanResult result = dynamoDB.scan(scanRequest);

        Map<String,Long> dishes = new HashMap<>();

        for(var it: result.getItems()) {
            dishes.put(it.get("dishId").getS(), dishes.getOrDefault(it.get("dishId").getS(), 0L) + Long.parseLong(it.get("totalSold").getS()));
        }
        // Sort by totalSold (convert from String to Integer)
        return dishes.entrySet().stream()
                .sorted((item1, item2) -> {
                    long sold1 = item1.getValue();
                    long sold2 = item2.getValue();
                    return Long.compare(sold2, sold1);
                })
                .limit(4)
                .map(Map.Entry::getKey) // Extract top dishIds
                .map(this::getDishById)
                .map(item -> new JSONObject()
                .put("name", item.get("name").getS())
                .put("price", item.get("price").getS())
                .put("weight", item.get("weight").getS())
                .put("imageUrl", item.get("imageUrl").getS()))
                .toList();
    }

    public Map<String, AttributeValue> getDishById (String id) {
        if(id==null || id.isEmpty()){
            throw new IllegalArgumentException("id is not present");
        }
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(id));

        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(dishesTable)
                .withKey(key);

        return dynamoDB.getItem(getItemRequest).getItem();
    }

    public List<JSONObject> getSpecialityDishes(String locationId) {
        if(locationId==null || locationId.isEmpty()){
            throw new IllegalArgumentException("LocationID is not present");
        }
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":locId", new AttributeValue().withS(locationId));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName(specialityDishesTable)
                .withKeyConditionExpression("locationId = :locId") // Use KeyConditionExpression
                .withExpressionAttributeValues(expressionValues);

        List<JSONObject> specialtyDishes = dynamoDB.query(queryRequest).getItems()
                .stream()
                .map(item -> item.get("dishId").getS())
                .map(this::getDishById)
                .map(item -> new JSONObject()
                        .put("name", item.get("name").getS())
                        .put("price", item.get("price").getS())
                        .put("weight", item.get("weight").getS())
                        .put("imageUrl", item.get("imageUrl").getS()))
                .collect(Collectors.toList());

        if(specialtyDishes.isEmpty()) {
            throw new RuntimeException("No specialty dish exists for the location provided");
        }
        return specialtyDishes;
    }

    public void checkDishType(String dishType) {
        try {
            DishType.valueOf(dishType);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("There is no such dish type");
        }
    }

    public List<Map<String, AttributeValue>> getAllDishesByType(String dishType) {
        ScanRequest scanRequest;
        if(dishType.isEmpty()) {
            scanRequest = new ScanRequest()
                    .withTableName(dishesTable);
        }
        else {
            scanRequest = new ScanRequest()
                    .withTableName(dishesTable)
                    .withFilterExpression("dishType = :type")
                    .withExpressionAttributeValues(Map.of(":type", new AttributeValue().withS(dishType)));
        }

        List<Map<String, AttributeValue>> dishes = dynamoDB.scan(scanRequest).getItems();
        if(dishes == null) {
            throw new RuntimeException("No dishes found for the provided dish type");
        }

        return dishes;
    }

    public List<Map<String,AttributeValue>> sortDishes(List<Map<String,AttributeValue>> dishes, String sortType, String sortOrder) {
        if(sortOrder.equals("asc")) {
            return dishes.stream()
                    .sorted(Comparator.comparingInt(dish -> Integer.parseInt(dish.get(sortType).getS())))
                    .toList();
        }

        return dishes.stream()
                    .sorted((dish1, dish2) -> Integer.parseInt(dish2.get(sortType).getS()) - Integer.parseInt(dish1.get(sortType).getS()))
                    .toList();

    }

    public String getSalesByDishId(String dishId) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(salesTable)
                .withFilterExpression("dishId = :dishid")
                .withExpressionAttributeValues(Map.of(":dishid",new AttributeValue().withS(dishId)));

        return Long.valueOf(dynamoDB.scan(scanRequest)
                .getItems()
                .stream()
                .mapToLong(item -> Long.parseLong(item.get("totalSold").getS()))
                .sum()).toString()
                ;
    }


    public List<JSONObject> getAllDishesByTypeAndSort(String dishType, String sortType, String sortOrder, Context context) {
        List<Map<String, AttributeValue>> allDishesByType = getAllDishesByType(dishType);
        context.getLogger().log("allDishesByType :-----" + allDishesByType);
        List<Map<String, AttributeValue>> dishes = allDishesByType.stream()
                .peek(item -> item.put("popularity", new AttributeValue().withS(getSalesByDishId(item.get("id").getS()))))
                .toList();
        context.getLogger().log("dishes :-----" + dishes);
        List<Map<String, AttributeValue>> sortedDishes = sortDishes(dishes, sortType, sortOrder);
        context.getLogger().log("sortedDishes :-----" + sortedDishes);
        return sortedDishes.stream()
                .map(item -> new JSONObject()
                        .put("id", item.get("id").getS())
                        .put("name", item.get("name").getS())
                        .put("previewImageUrl", item.get("imageUrl").getS())
                        .put("price", item.get("price").getS())
                        .put("state", getDishStateFromOverallStock(item.get("id").getS()))
                        .put("weight", item.get("weight").getS())
                )
                .toList();
    }

    public String getDishStateFromOverallStock(String dishId) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(salesTable)
                .withFilterExpression("dishId = :id")
                .withExpressionAttributeValues(Map.of(":id", new AttributeValue().withS(dishId)));

        List<Map<String, AttributeValue>> stocks = dynamoDB.scan(scanRequest).getItems();

        Long stock = Long.valueOf(stocks.stream()
                .mapToLong(item -> Long.parseLong(item.get("stock").getS()))
                .sum());

        if(stock == 0) return "On Stop";
        else return "Available";
    }

    public boolean checkLocationExists(String locationId) {
        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(dishesTable)
                .withKey(Map.of("id", new AttributeValue().withS(locationId)));

        Map<String, AttributeValue> location = dynamoDB.getItem(getItemRequest)
                .getItem();

        return !(location == null);
    }

    private String getSalesByDishIdAndLocation(String dishId, String locationId) {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(salesTable)
                .withKeyConditionExpression("locationId = :locationId AND dishId = :dishid")
                .withExpressionAttributeValues(Map.of(":dishid",new AttributeValue().withS(dishId), ":locationId", new AttributeValue().withS(locationId)));

        return Long.valueOf(dynamoDB.query(queryRequest)
                .getItems()
                .stream()
                .mapToLong(item -> Long.parseLong(item.get("totalSold").getS()))
                .sum()).toString()
                ;
    }

    private String getDishStateFromOverallStockAtALocation(String locationId, String dishId) {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(salesTable)
                .withKeyConditionExpression("locationId = :locationId AND dishId = :dishid")
                .withExpressionAttributeValues(Map.of(":dishid",new AttributeValue().withS(dishId), ":locationId", new AttributeValue().withS(locationId)));

        List<Map<String, AttributeValue>> stocks = dynamoDB.query(queryRequest).getItems();

        Long stock = Long.valueOf(stocks.stream()
                .mapToLong(item -> Long.parseLong(item.get("stock").getS()))
                .sum());

        if(stock == 0) return "On Stop";
        else return "Available";
    }

    public List<JSONObject> getDishesAtALocation(String locationId, String dishType, String sortType, String sortOrder, Context context) {

        List<Map<String, AttributeValue>> allDishesByType = getAllDishesByType(dishType);
        context.getLogger().log("allDishesByType :-----" + allDishesByType);
        List<Map<String, AttributeValue>> dishes = allDishesByType.stream()
                .peek(item -> item.put("popularity", new AttributeValue().withS(getSalesByDishIdAndLocation(item.get("id").getS(),locationId))))
                .toList();
        context.getLogger().log("dishes :-----" + dishes);
        List<Map<String, AttributeValue>> sortedDishes = sortDishes(dishes, sortType, sortOrder);
        context.getLogger().log("sortedDishes :-----" + sortedDishes);
        return sortedDishes.stream()
                .map(item -> new JSONObject()
                        .put("id", item.get("id").getS())
                        .put("name", item.get("name").getS())
                        .put("previewImageUrl", item.get("imageUrl").getS())
                        .put("price", item.get("price").getS())
                        .put("state", getDishStateFromOverallStockAtALocation(locationId,item.get("id").getS()))
                        .put("weight", item.get("weight").getS())
                )
                .toList();
    }


    public List<JSONObject> getDishItems(List<Map<String, AttributeValue>> orders) {
        List<JSONObject> dishes = new ArrayList<>();

        for(Map<String, AttributeValue> m:orders)
        {
            Map<String, AttributeValue> dish = getDishById(m.get("dishId").getS());
            dishes.add(new JSONObject()
                    .put("dishId", dish.get("id").getS())
                    .put("dishImageUrl", dish.get("imageUrl").getS())
                    .put("dishName", dish.get("name").getS())
                    .put("dishPrice", dish.get("price").getS())
                    .put("dishQuantity", m.get("quantity").getN())
            );
        }
        return dishes;
    }
}
