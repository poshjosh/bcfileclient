package com.looseboxes.fileclient.config.aws;

/**
 * @author hp
 */
public class AwsProperties {
    
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String imagesBucketName;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getImagesBucketName() {
        return imagesBucketName;
    }

    public void setImagesBucketName(String imagesBucketName) {
        this.imagesBucketName = imagesBucketName;
    }

    @Override
    public String toString() {
        return "AwsProperties{" + "accessKey=*******, secretKey=*******, bucketName=" + 
                bucketName + ", imagesBucketName=" + imagesBucketName + '}';
    }
}
