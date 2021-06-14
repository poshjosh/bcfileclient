package com.looseboxes.fileclient.aws;

import com.looseboxes.fileclient.FileHandlerFactory;
import com.amazonaws.services.s3.AmazonS3Client;
import com.looseboxes.fileclient.FileHandler;
import com.looseboxes.fileclient.config.aws.AwsProperties;
import java.util.Objects;

/**
 * @author hp
 */
public class AwsS3FileHandlerFactory implements FileHandlerFactory{
    
    private final AmazonS3Client awsS3Client;
    private final AwsFileHandler.S3FileKeyBuilder s3FileKeyBuilder; 
    private final AwsProperties awsProperties;

    public AwsS3FileHandlerFactory(
            AmazonS3Client awsS3Client, 
            AwsFileHandler.S3FileKeyBuilder s3FileKeyBuilder, 
            AwsProperties awsProperties) {
        this.awsS3Client = Objects.requireNonNull(awsS3Client);
        this.s3FileKeyBuilder = Objects.requireNonNull(s3FileKeyBuilder);
        this.awsProperties = Objects.requireNonNull(awsProperties);
    }
            
    @Override
    public FileHandler get(FileSystemType fileSystemType) {
        String bucketName = this.getBucketName(fileSystemType);
        return new AwsFileHandler(bucketName, awsS3Client, s3FileKeyBuilder);
    }

    private String getBucketName(FileSystemType fileSystemType) {
        switch(fileSystemType) {
            case BASE:
                return awsProperties.getBucketName();
            case IMAGES:
                return awsProperties.getImagesBucketName();
            default:
                throw new IllegalArgumentException("FileSystemType: " + fileSystemType);
        }
    }
}
