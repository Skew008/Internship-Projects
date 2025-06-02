package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.restaurantbackend.dto.ReportContent;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class ReportService {
    private final AmazonDynamoDB dynamoDB;
    private final String reservationsTable;
    private final String reportTable;
    private final String locationsTable;
    private final String feedbackTable;
    private final String orderTable;
    private final String dishesTable;
    private final UserService userService;
    private final String receiverEmail;
    private final String reportBucket;
    private final String senderEmail;
    private final AmazonS3 s3Client;
    private final AmazonSimpleEmailService sesClient;

    public ReportService(AmazonDynamoDB dynamoDB , AmazonS3 s3Client , UserService userService) {
        this.dynamoDB = dynamoDB;
        this.s3Client = s3Client;
        this.userService = userService;
        this.feedbackTable = System.getenv("FEEDBACKS_TABLE");
        this.locationsTable = System.getenv("LOCATIONS_TABLE");
        this.reservationsTable = System.getenv("RESERVATIONS_TABLE");
        this.reportTable = System.getenv("REPORT_TABLE");
        this.orderTable = System.getenv("ORDERS_TABLE");
        this.dishesTable = System.getenv("DISHES_TABLE");
        this.reportBucket = System.getenv("S3_BUCKET");
        this.senderEmail = System.getenv("SES_SENDER_EMAIL");
        this.receiverEmail = System.getenv("SES_RECEIVER_EMAIL");
        this.sesClient = AmazonSimpleEmailServiceClientBuilder.defaultClient();
    }

    public void processReservationInQueue(String reservation_id)
    {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(reservation_id));
        GetItemRequest getItemRequest = new GetItemRequest()
                .withTableName(reservationsTable)
                .withKey(key);

        Map<String, AttributeValue> item = dynamoDB.getItem(getItemRequest).getItem();

        Map<String, AttributeValue> map = Map.of("id", new AttributeValue().withS(UUID.randomUUID().toString()),
                "locationId", new AttributeValue().withS(item.get("locationId").getS()),
                "email", new AttributeValue().withS(item.get("waiterEmail").getS()),
                "date", new AttributeValue().withS(item.get("date").getS()),
                "feedbackId", new AttributeValue().withS(item.get("feedbackId").getS()),
                "orderId" ,new AttributeValue().withS(item.get("orderId").getS())
        );

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(reportTable)
                .withItem(map);
        dynamoDB.putItem(putItemRequest);
    }

    public Double hoursWaiterWorked(String waiter_email , String startDate , String endDate){
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        return  items.stream().filter(e->e.get("email").getS().equals(waiter_email)).filter(e->{
            LocalDate date = LocalDate.parse(e.get("date").getS());
            return !date.isBefore(LocalDate.parse(startDate)) && !date.isAfter(LocalDate.parse(endDate));
        }).count()*1.5;
    }

    public Integer orderProcessedByEachWaiter(String waiter_email , String startDate , String endDate) {
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        return (int) items.stream().filter(e->e.get("email").getS().equals(waiter_email)).filter(e->{
            LocalDate date = LocalDate.parse(e.get("date").getS());
            return !date.isBefore(LocalDate.parse(startDate)) && !date.isAfter(LocalDate.parse(endDate));
        }).count();
    }

    public Double deltaOfOrdersProcesses(String waiter_email , String startDate , String endDate)
    {
        LocalDate dates[] = getSlots(startDate, endDate);
       int previous =  orderProcessedByEachWaiter(waiter_email, dates[0].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dates[1].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
       int current =  orderProcessedByEachWaiter(waiter_email, dates[2].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), dates[3].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

       int delta = Math.abs(current - previous)*100;
       return (double) delta/previous;
    }

    public Integer minimumServiceFeedback(String waiter_email , String startDate , String endDate)
    {
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        items = items.stream().filter(e -> {
            LocalDate date = LocalDate.parse(e.get("date").getS());
            return !date.isBefore(LocalDate.parse(startDate)) && !date.isAfter(LocalDate.parse(endDate));
        }).collect(Collectors.toList());
        return  minFeedback(waiter_email , items);
    }

    public Double averageServiceFeedback(String waiter_email , String startDate , String endDate)
    {
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        Set<String> feedbackIds = items.stream().filter(e -> e.get("email").getS().equals(waiter_email)).filter(e -> {
            LocalDate date = LocalDate.parse(e.get("date").getS());
            return !date.isBefore(LocalDate.parse(startDate)) && !date.isAfter(LocalDate.parse(endDate));
        }).map(e -> e.get("feedbackId").getS()).collect(Collectors.toSet());
        List<Map<String, AttributeValue>> feedbacks = dynamoDB.scan(new ScanRequest(feedbackTable)).getItems();
       return feedbacks.stream().filter(e->e.get("type").getS().equals("SERVICE") && feedbackIds.contains(e.get("id").getS())).mapToInt(e->Integer.parseInt(e.get("rate").getS())).average().orElse(0);
    }

    public Double deltaAverageFeedbackService(String waiterEmail , String startDate , String endDate)
    {
       LocalDate[] dates = getSlots(startDate, endDate);
        double previous = averageServiceFeedback(waiterEmail,startDate,endDate);
        double current = averageServiceFeedback(waiterEmail, startDate,endDate);

        double diff = Math.abs(current - previous)*100;
        return (double) diff/previous;
    }

    public Integer minFeedback(String waiter_email, List<Map<String, AttributeValue>>  items){
        Set<String> feedbackIds = items.stream().filter(e -> e.get("email").getS().equals(waiter_email)).map(e -> e.get("feedbackId").getS()).collect(Collectors.toSet());
        List<Map<String, AttributeValue>> feedbacks = dynamoDB.scan(new ScanRequest(feedbackTable)).getItems();
        return feedbacks.stream().filter(e->e.get("type").getS().equals("SERVICE") && feedbackIds.contains(e.get("id").getS())).mapToInt(e->Integer.parseInt(e.get("rate").getS())).min().orElse(0);
    }

    public LocalDate[] getSlots(String startDate , String endDate)
    {
        //  date format is yyyy-MM-dd
        LocalDate currentStartDate = LocalDate.parse(startDate);
        LocalDate currentEndDate = LocalDate.parse(endDate);
        Period period = Period.between(currentStartDate, currentEndDate);
        int days_diff = period.getDays();
        LocalDate previousEndDate = currentStartDate.minusDays(1);
        LocalDate previousStartDate = previousEndDate.minusDays(days_diff);

        LocalDate[] arr = new LocalDate[4];
        arr[0] = previousStartDate;
        arr[1] = previousEndDate;
        arr[2] = currentStartDate;
        arr[3] = currentEndDate;
        return arr;
    }

    public Integer ordersProcessedWithinTheLocation(String locationId , String startDate , String endDate)
    {
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        return (int) items.stream().filter(e -> e.get("locationId").getS().equals(locationId))
                .filter(e -> {
                    LocalDate date = LocalDate.parse(e.get("date").getS());
                    return !date.isBefore(LocalDate.parse(startDate)) && !date.isAfter(LocalDate.parse(endDate));
                }).count();
    }

    public Double deltaOrdersProcessedWithinTheLocation(String locationId , String startDate , String endDate)
    {
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        LocalDate[] dates = getSlots(startDate , endDate);
        Integer previous = (int) items.stream().filter(e -> e.get("locationId").getS().equals(locationId))
                .filter(e -> {
                    LocalDate date = LocalDate.parse(e.get("date").getS());
                    return !date.isBefore(dates[0]) && !date.isAfter(dates[1]);
                }).count();

        Integer current = (int) items.stream().filter(e -> e.get("locationId").getS().equals(locationId))
                .filter(e -> {
                    LocalDate date = LocalDate.parse(e.get("date").getS());
                    return !date.isBefore(dates[2]) && !date.isAfter(dates[3]);
                }).count();

        int delta = Math.abs(current - previous)*100;
        return (double)delta/previous;
    }

    public Double averageCuisineFeedback(String locationId , String startDate , String endDate){
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        Set<String> set = items.stream().filter(e -> e.get("locationId").getS().equals(locationId)).map(e -> e.get("feedbackId").getS()).collect(Collectors.toSet());
        List<Map<String, AttributeValue>> feedbacks = dynamoDB.scan(new ScanRequest(feedbackTable)).getItems();
       return  feedbacks.stream().filter(e->set.contains(e.get("id").getS()) && e.get("type").getS().equals("CUISINE")).mapToInt(e->Integer.parseInt(e.get("rate").getS())).average().orElse(0);
    }

    public Integer minimumCuisineFeedback(String locationId , String startDate , String endDate){
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        Set<String> set = items.stream().filter(e -> e.get("locationId").getS().equals(locationId)).map(e -> e.get("feedbackId").getS()).collect(Collectors.toSet());
        List<Map<String, AttributeValue>> feedbacks = dynamoDB.scan(new ScanRequest(feedbackTable)).getItems();
        return feedbacks.stream().filter(e->set.contains(e.get("id").getS()) && e.get("type").getS().equals("CUISINE")).mapToInt(e->Integer.parseInt(e.get("rate").getS())).min().orElse(0);
    }

    public Double deltaOfAverageCuisineFeedback(String locationId , String startDate , String endDate) {
        LocalDate dates[] = getSlots(startDate , endDate);
        double previous = averageCuisineFeedback(locationId , dates[0].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) , dates[1].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        double current = averageCuisineFeedback(locationId , dates[2].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) , dates[3].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        double delta = Math.abs(current - previous)*100;
        return delta/previous;
    }

    public Double revenueForOrderProcessed(String locationId , String startDate , String endDate)
    {
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        Set<String> set = items.stream().filter(e -> e.get("locationId").getS().equals(locationId)).filter(e -> {
            LocalDate date = LocalDate.parse(e.get("date").getS());
            return !date.isBefore(LocalDate.parse(startDate)) && !date.isAfter(LocalDate.parse(endDate));
        }).map(e->e.get("orderId").getS()).collect(Collectors.toSet());

        List<Map<String, AttributeValue>> orders = dynamoDB.scan(new ScanRequest(orderTable)).getItems();
        List<Map<String , AttributeValue>> list = orders.stream().filter(e->set.contains(e.get("id").getS())).toList();
        List<Map<String, AttributeValue>> dishes = dynamoDB.scan(new ScanRequest(dishesTable)).getItems();
        return (double) list.stream().map(e->{
           Map<String, AttributeValue> any = dishes.stream().filter(dish -> dish.get("id").getS().equals(e.get("dishId").getS())).findAny().orElseThrow(()->{throw new IllegalArgumentException("not found");});
            return  Integer.parseInt(any.get("price").getS())*Integer.parseInt(e.get("quantity").getN());
        }).mapToInt(e->e).sum();
    }

    public Double deltaRevenueForOrderProcessed(String locationId , String startDate , String endDate)
    {
        LocalDate[] dates = getSlots(startDate, endDate);
        double previous = revenueForOrderProcessed(locationId , dates[0].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) , dates[1].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        double current = revenueForOrderProcessed(locationId , dates[2].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) , dates[3].format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        double delta = Math.abs(current - previous)*100;
        return delta/previous;
    }

    public String[] generateFile(LocalDate start  , LocalDate end ){
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        String waiters = waiterFeedback(items , start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) , end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String locations = locationFeedback(items , start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) , end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String[] arr = {waiters , locations };
        return arr;
    }

    public String waiterFeedback(List<Map<String, AttributeValue>> items, String startDate , String endDate)
    {
        StringBuilder sb = new StringBuilder("Location,Waiter,Waiter's e-mail,Report period start,Report period end,Waiter working hours,Waiter Orders processed,Delta of Waiter Orders processed to previous period in %,Average Service Feedback Waiter (1 to 5),Minimum Service Feedback Waiter (1 to 5),Delta of Average Service Feedback Waiter to previous period in %\n");
        Set<String> waiter = new HashSet<>();
        items.forEach(e->{
            if(!waiter.contains(e.get("email").getS())) {

                waiter.add(e.get("email").getS());
                Map<String, AttributeValue> user = userService.getUserByEmail(e.get("email").getS());

                sb.append(e.get("locationId").getS()).append(",")
                        .append(user.get("firstName").getS() + user.get("lastName").getS()).append(",")
                        .append(e.get("email").getS()).append(",")
                        .append(startDate).append(",")
                        .append(endDate).append(",")
                        .append(hoursWaiterWorked(e.get("email").getS(), startDate, endDate)).append(",")
                        .append(orderProcessedByEachWaiter(e.get("email").getS(), startDate, endDate)).append(",")
                        .append(deltaOfOrdersProcesses(e.get("email").getS(), startDate, endDate)).append(",")
                        .append(averageServiceFeedback(e.get("email").getS(), startDate, endDate)).append(",")
                        .append(minimumServiceFeedback(e.get("email").getS(), startDate, endDate)).append(",")
                        .append(deltaAverageFeedbackService(e.get("email").getS(), startDate, endDate)).append(",\n");
            }});
        return sb.toString();
    }

    public String waiterFeedbackBasedOnWaiter(List<Map<String, AttributeValue>> items , String startDate , String endDate , String waiter_email)
    {
        StringBuilder sb = new StringBuilder("Location,Waiter,Waiter's e-mail,Report period start,Report period end,Waiter working hours,Waiter Orders processed,Delta of Waiter Orders processed to previous period in %,Average Service Feedback Waiter (1 to 5),Minimum Service Feedback Waiter (1 to 5),Delta of Average Service Feedback Waiter to previous period in %\n");
        Set<String> waiter = new HashSet<>();
        Map<String, AttributeValue> user = userService.getUserByEmail(waiter_email);
        items.stream().filter(e->e.get("email").getS().equals(waiter_email)).limit(1).forEach(e->{
            sb.append(e.get("locationId").getS()).append(",")
                    .append(user.get("firstName").getS() + user.get("lastName").getS()).append(",")
                    .append(e.get("email").getS()).append(",")
                    .append(startDate).append(",")
                    .append(endDate).append(",")
                    .append(hoursWaiterWorked(e.get("email").getS(), startDate, endDate)).append(",")
                    .append(orderProcessedByEachWaiter(e.get("email").getS(), startDate, endDate)).append(",")
                    .append(deltaOfOrdersProcesses(e.get("email").getS(), startDate, endDate)).append(",")
                    .append(averageServiceFeedback(e.get("email").getS(), startDate, endDate)).append(",")
                    .append(minimumServiceFeedback(e.get("email").getS(), startDate, endDate)).append(",")
                    .append(deltaAverageFeedbackService(e.get("email").getS(), startDate, endDate)).append(",\n");

        });
        return sb.toString();
    }


    public String locationFeedbackBasedOnLocation(List<Map<String, AttributeValue>> items , String startDate , String endDate , String location_id)
    {
        StringBuilder sb = new StringBuilder("Location,Report period start,Report period end,Orders processed within location,Delta of orders processed within location to previous period (in %),Average cuisine Feedback by Restaurant location (1 to 5),Minimum cuisine Feedback by Restaurant location (1 to 5),Delta of average cuisine Feedback by Restaurant location to previous period (in %),Revenue for orders within reported period,Delta of revenue for orders to previous period %\n");
        Set<String> loc = new HashSet<>();

        Map<String, AttributeValue> location = items.stream().filter(e -> e.get("locationId").getS().equals(location_id)).findFirst().orElse(null);
        sb.append(location.get("locationId").getS()).append(",")
                .append(startDate).append(",")
                .append(endDate).append(",")
                .append(ordersProcessedWithinTheLocation(location.get("locationId").getS(), startDate, endDate)).append(",")
                .append(deltaOrdersProcessedWithinTheLocation(location.get("locationId").getS(), startDate, endDate)).append(",")
                .append(averageCuisineFeedback(location.get("locationId").getS(), startDate, endDate)).append(",")
                .append(minimumCuisineFeedback(location.get("locationId").getS(), startDate, endDate)).append(",")
                .append(deltaOfAverageCuisineFeedback(location.get("locationId").getS(), startDate, endDate)).append(",")
                .append(revenueForOrderProcessed(location.get("locationId").getS(), startDate, endDate)).append(",")
                .append(deltaRevenueForOrderProcessed(location.get("locationId").getS(), startDate, endDate)).append(",\n");

        return sb.toString();
    }


    public String locationFeedback(List<Map<String, AttributeValue>> items , String startDate , String endDate)
    {
        StringBuilder sb = new StringBuilder("Location,Report period start,Report period end,Orders processed within location,Delta of orders processed within location to previous period (in %),Average cuisine Feedback by Restaurant location (1 to 5),Minimum cuisine Feedback by Restaurant location (1 to 5),Delta of average cuisine Feedback by Restaurant location to previous period (in %),Revenue for orders within reported period,Delta of revenue for orders to previous period %\n");
        Set<String> loc = new HashSet<>();
        items.stream().forEach(e->{
            if(!loc.contains(e.get("locationId").getS())) {

                loc.add(e.get("locationId").getS());
                sb.append(e.get("locationId").getS()).append(",")
                        .append(startDate).append(",")
                        .append(endDate).append(",")
                        .append(ordersProcessedWithinTheLocation(e.get("locationId").getS(), startDate, endDate)).append(",")
                        .append(deltaOrdersProcessedWithinTheLocation(e.get("locationId").getS(), startDate, endDate)).append(",")
                        .append(averageCuisineFeedback(e.get("locationId").getS(), startDate, endDate)).append(",")
                        .append(minimumCuisineFeedback(e.get("locationId").getS(), startDate, endDate)).append(",")
                        .append(deltaOfAverageCuisineFeedback(e.get("locationId").getS(), startDate, endDate)).append(",")
                        .append(revenueForOrderProcessed(e.get("locationId").getS(), startDate, endDate)).append(",")
                        .append(deltaRevenueForOrderProcessed(e.get("locationId").getS(), startDate, endDate)).append(",\n");
            }});
        return sb.toString();
    }



    public void generateAndSendReports() {
        try {
            // Set the time zone to IST (Indian Standard Time)
            ZoneId istZone = ZoneId.of("Asia/Kolkata");
            ZonedDateTime today = ZonedDateTime.now(istZone);
            ZonedDateTime weekAgo = today.minusDays(7);
            LocalDate end = today.toLocalDate();
            LocalDate start = weekAgo.toLocalDate();

            String[] files = generateFile(start , end);

            String waiterFileName = "waiter_report_" + end + ".csv";
            String locationFileName = "location_report_" + end + ".csv";

            // Upload reports to S3
            String waiterReportKey = uploadReportToS3(files[0], waiterFileName);
            String locationReportKey = uploadReportToS3(files[1], locationFileName);
//
//            // Store the report metadata in DynamoDB
//            storeReportMetadata("N/A", "Waiter Weekly Report", "Weekly performance of waiters", waiterReportKey, "N/A", "Staff", weekAgoLocal, todayLocal);
//            storeReportMetadata("N/A", "Location Weekly Report", "Weekly performance by location", locationReportKey, "N/A", "Location", weekAgoLocal, todayLocal);

            // Send email with report links
//            logger.info("Sending email with report links.");
            sendEmailWithReports(Arrays.asList(waiterFileName, locationFileName));
//            logger.info("Weekly reports generated and dispatched successfully.");
        } catch (Exception e) {
            e.printStackTrace();
//            logger.severe("Error generating or dispatching reports: " + e.getMessage());
        }
    }



    private String uploadReportToS3(String csvContent, String fileName) {
        byte[] contentAsBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentAsBytes.length);
        metadata.setContentType("text/csv");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(contentAsBytes);

        // Use PutObjectRequest
        PutObjectRequest request = new PutObjectRequest(reportBucket, fileName, inputStream, metadata);

        s3Client.putObject(request);

        // Return the static public URL
        return s3Client.generatePresignedUrl(reportBucket, fileName, new Date(System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(3))).toString();
    }

    private void sendEmailWithReports(List<String> fileNames) {
        try {
            MimeMultipart multipart = new MimeMultipart();

            // Add email body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Attached are the weekly reports for waiter and location performance.", "utf-8");
            multipart.addBodyPart(textPart);

            // Add each CSV file from S3 as an attachment
            for (String fileName : fileNames) {
                S3Object s3Object = s3Client.getObject(new GetObjectRequest(reportBucket, fileName));
                InputStream inputStream = s3Object.getObjectContent();

                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setFileName(fileName);
                attachmentPart.setContent(inputStream.readAllBytes(), "text/csv");
                attachmentPart.setHeader("Content-Type", "text/csv");

                multipart.addBodyPart(attachmentPart);
            }

            // Create the full email message
            MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject("Weekly Reports");
            message.setContent(multipart);

            // Convert the MimeMessage to raw bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            ByteBuffer rawMessage = ByteBuffer.wrap(outputStream.toByteArray());

            // Send using AWS SES
            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest()
                    .withRawMessage(new RawMessage().withData(rawMessage));

            sesClient.sendRawEmail(rawEmailRequest);

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email with reports", e);
        }
    }

    public String[] reportApi(List<Map<String , AttributeValue>> items , String type, String locationId , String waiterEmail ,  String start, String end)
    {
        String[] dates = setStartEndDate(start , end);
//        if(locationId==null && waiterEmail==null){
//            return new String[]{waiterFeedback(items , dates[0] , dates[1]) , locationFeedback(items ,  dates[0] , dates[1])};
//        }else if(locationId==null || waiterEmail==null){
//            if(locationId==null){
//                return new String[]{waiterFeedbackBasedOnWaiter(items ,  dates[0] , dates[1], waiterEmail)};
//            }else{
//                return new String[]{locationFeedbackBasedOnLocation(items ,  dates[0] , dates[1] , locationId)};
//            }
//        }else{
//            return new String[]{waiterFeedbackBasedOnWaiter(items ,  dates[0] , dates[1], waiterEmail)};
//        }

        if(type.equalsIgnoreCase("sales"))
        {
            if(locationId!=null)
                return new String[]{locationFeedbackBasedOnLocation(items ,  dates[0] , dates[1] , locationId)};
            else
                return new String[]{locationFeedback(items ,  dates[0] , dates[1])};
        } else if (type.equalsIgnoreCase("staff performance")) {
            if(waiterEmail!=null)
                return new String[]{waiterFeedbackBasedOnWaiter(items ,  dates[0] , dates[1], waiterEmail)};
            else if (locationId!=null)
                return new String[]{waiterFeedbackBasedOnLocation(items ,  dates[0] , dates[1] , locationId)};
            else
                return new String[]{waiterFeedback(items ,  dates[0] , dates[1])};
        }
        else
            throw new IllegalArgumentException("Invalid report type: " + type);
    }

    private String waiterFeedbackBasedOnLocation(List<Map<String, AttributeValue>> items, String startDate, String endDate, String locationId) {
        {
            StringBuilder sb = new StringBuilder("Location,Waiter,Waiter's e-mail,Report period start,Report period end,Waiter working hours,Waiter Orders processed,Delta of Waiter Orders processed to previous period in %,Average Service Feedback Waiter (1 to 5),Minimum Service Feedback Waiter (1 to 5),Delta of Average Service Feedback Waiter to previous period in %\n");
            Set<String> waiter = new HashSet<>();
            items.stream().filter(e->e.get("locationId").getS().equalsIgnoreCase(locationId)).forEach(e->{
                if(!waiter.contains(e.get("email").getS())) {

                    waiter.add(e.get("email").getS());
                    Map<String, AttributeValue> user = userService.getUserByEmail(e.get("email").getS());

                    sb.append(e.get("locationId").getS()).append(",")
                            .append(user.get("firstName").getS() + user.get("lastName").getS()).append(",")
                            .append(e.get("email").getS()).append(",")
                            .append(startDate).append(",")
                            .append(endDate).append(",")
                            .append(hoursWaiterWorked(e.get("email").getS(), startDate, endDate)).append(",")
                            .append(orderProcessedByEachWaiter(e.get("email").getS(), startDate, endDate)).append(",")
                            .append(deltaOfOrdersProcesses(e.get("email").getS(), startDate, endDate)).append(",")
                            .append(averageServiceFeedback(e.get("email").getS(), startDate, endDate)).append(",")
                            .append(minimumServiceFeedback(e.get("email").getS(), startDate, endDate)).append(",")
                            .append(deltaAverageFeedbackService(e.get("email").getS(), startDate, endDate)).append(",\n");
                }});
            return sb.toString();
        }

    }

    public String generateReportDownloadLink(String report , String name , String end)
    {
        String reportName = name + "_" + end + ".csv";
        return uploadReportToS3(report , reportName);
    }

    public List<ReportContent> mainReportGetApi(String reportType, String locationId , String waiterEmail ,  String start, String end)
    {
        List<Map<String, AttributeValue>> items = dynamoDB.scan(new ScanRequest(reportTable)).getItems();
        String[] files = reportApi(items , reportType, locationId ,  waiterEmail , start , end);
        int count = 1;
        List<ReportContent> list = new ArrayList<>();
        for(String file:files)
        {
            String link = generateReportDownloadLink(file , "Report - " + count , end);
            list.add(new ReportContent(getDescription(locationId ,  waiterEmail , start , end) , link , start==null?"N/A":start , locationId==null?"N/A":locationId ,"Report - " + count  , end==null?"N/A":end ,  waiterEmail==null?"N/A":waiterEmail, csvToListOfMaps(file)));
            count++;
        }
        return list;
    }

    public String getDescription(String locationId , String waiterEmail ,  String start, String end)
    {
        StringBuilder str = new StringBuilder("Report with ");
        if(locationId==null && waiterEmail==null){
            str.append("only start and end Date.");
        }else if(locationId==null || waiterEmail==null){
            if(locationId==null){
                str.append("start date , end date and location id .");
            }else{
                str.append("start date , end date and waiter email .");
            }
        }else{
            str.append("start date , end date , waiter email and location id.");
        }
        return str.toString();
    }

    public String[] setStartEndDate(String startDate , String endDate)
    {
        String start = "";
        String end = "";
        if(startDate==null && endDate==null){
            start = LocalDate.now().minusDays(7).toString();
            end = LocalDate.now().toString();
        }else if(startDate==null || endDate==null){
            if(endDate==null){
                end = LocalDate.now().toString();
            }else{
                start = LocalDate.parse(endDate).minusDays(7).toString();
            }
        }else{
            start = startDate;
            end = endDate;
        }
        return new String[]{start , end};
    }

    public List<Map<String, String>> csvToListOfMaps(String csvContent) {
        List<Map<String, String>> result = new ArrayList<>();
        String[] lines = csvContent.split("\n");

//        if (lines.length < 2) {
//            return result; // Return empty list if no data
//        }

        // Extract headers
        String[] headers = lines[0].split(",");

        // Process each line after the header
        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            if (values.length != headers.length) {
                continue; // Skip malformed lines
            }

            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                map.put(headers[j].trim(), values[j].trim());
            }
            result.add(map);
        }

        return result;
    }
}


