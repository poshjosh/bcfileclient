package com.looseboxes.fileclient.aws;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.looseboxes.fileclient.FileHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hp
 */
public class AwsFileHandler implements FileHandler{
    
    private final Logger log = LoggerFactory.getLogger(AwsFileHandler.class);
    
    public static interface S3FileKeyBuilder{
        String from(Path path);
    }
    
    private final String s3BucketName;
    private final AmazonS3Client awsS3client;
    private final S3FileKeyBuilder s3FileKey;

    public AwsFileHandler(String s3BucketName, AmazonS3Client awsS3client, S3FileKeyBuilder s3FileKey) {
        this.s3BucketName = Objects.requireNonNull(s3BucketName);
        this.awsS3client = Objects.requireNonNull(awsS3client);
        this.s3FileKey = Objects.requireNonNull(s3FileKey);
    }

    @Override
    public InputStream read(Path from) throws IOException{
        
        final String s3filename = this.s3FileKey.from(from);
        
        try{
            
            log.debug("Reading from AWS S3 bucket: {}, filename: {}", s3BucketName, s3filename);
            
            return awsS3client.getObject(s3BucketName, s3filename).getObjectContent();
            
        }catch(RuntimeException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public void write(InputStream in, Path to, long contentLength, String contentType) 
            throws IOException{
    
        final String filename = this.s3FileKey.from(to);
        
        log.debug("Writing to S3 bucket: {}, filename: {}, content length: {}, type: {}",
                s3BucketName, filename, contentLength, contentType);
        
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(contentLength);
        metaData.setContentType(contentType);
        
        try{
            
            PutObjectRequest request = new PutObjectRequest(s3BucketName, filename, in, metaData)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            PutObjectResult result = awsS3client.putObject(request);
            
            log.debug("S3 response: {}", result);
            
        }catch(RuntimeException e) {
            throw new IOException(e);
        }
    }
    
    @Override
    public boolean delete(Path path) throws IOException {
        
        final String filename = this.s3FileKey.from(path);
        
        try{
            
            this.awsS3client.deleteObject(s3BucketName, filename);
            
            return true;
            
        }catch(RuntimeException e) {
            
            throw new IOException(e);
        }
    }
}
