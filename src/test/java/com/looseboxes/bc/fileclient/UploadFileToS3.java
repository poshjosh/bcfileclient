package com.looseboxes.bc.fileclient;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

/**
 * @author hp
 */
public class UploadFileToS3 {
    
    public static void main(String... args) {
        
        final String s3BucketName = "<YOUR_AWS_BUCKET_NAME>";
        final String awsAccessKey = "<YOUR_AWS_ACCESS_KEY>";
        final String awsSecretKey = "<YOUR_AWS_SECRET_KEY>";
        
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials); 
        
        final String content = "Sample content uploaded by " + System.getProperty("user.name") + " on " + ZonedDateTime.now();
        
        final String targetFilename = "sampleFile_DELETE_ME.txt";
        
        ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(content.length());
        metaData.setContentType("text/plain");
        
        try{
            
            PutObjectRequest request = new PutObjectRequest(s3BucketName, targetFilename, in, metaData)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            PutObjectResult result = s3Client.putObject(request);
            
            System.out.println("S3 response: " + result);
            
        }catch(RuntimeException e) {
            e.printStackTrace();
        }
    }
}
