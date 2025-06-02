package com.restaurantbackend.service;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class S3Service {
    private final AmazonS3 s3Client;
    private final String bucketName = System.getenv("USER_PROFILE_IMAGE_BUCKET");


    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }
    public String uploadBase64Image(String base64Image, String email, Context context) {
       context.getLogger().log(base64Image);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            String fileName = email.split("@")[0] + ".png";

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/png");

            InputStream inputStream = new ByteArrayInputStream(imageBytes);
            s3Client.putObject(bucketName, fileName, inputStream, metadata);
            return "https://team8-user-profile-images.s3.ap-northeast-1.amazonaws.com/"+ fileName;

    }
    public String getBase64ImageFromS3(String s3Url) {
        try {
            String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);
            S3Object object = s3Client.getObject(bucketName, fileName);
            InputStream inputStream = object.getObjectContent();
            byte[] imageBytes = inputStream.readAllBytes();

            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error reading image from S3 and converting to Base64", e);
        }
    }

}



