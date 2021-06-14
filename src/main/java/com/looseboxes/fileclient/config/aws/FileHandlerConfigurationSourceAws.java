package com.looseboxes.fileclient.config.aws;

import com.looseboxes.fileclient.config.FileHandlerConfigurationSource;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.looseboxes.fileclient.LocalFileHandler;
import com.looseboxes.fileclient.aws.AwsFileHandler;
import com.looseboxes.fileclient.aws.AwsS3FileHandlerFactory;
import com.looseboxes.fileclient.FileHandlerFactory;
import com.looseboxes.fileclient.FileHandlerFactoryComposite;
import com.looseboxes.fileclient.FileHandlerFactoryForDelegate;
import com.looseboxes.fileclient.FileHandlerFactoryWithCache;
import com.looseboxes.fileclient.aws.S3FileKeyBuilderImpl;
import java.util.Objects;
import org.springframework.context.annotation.Bean;

/**
 * @author hp
 */
public abstract class FileHandlerConfigurationSourceAws extends FileHandlerConfigurationSource{
    
    private final AwsProperties awsProperties;

    public FileHandlerConfigurationSourceAws(AwsProperties awsProperties) {
        this.awsProperties = Objects.requireNonNull(awsProperties);
    }
    
    protected AwsProperties getAwsProperties() {
        return awsProperties;
    }

    @Bean
    @Override
    public FileHandlerFactory fileHandlerFactory() {
        // Remote should come first
        final FileHandlerFactory remote = awsS3FileHandlerFactory();
        final FileHandlerFactory local = new FileHandlerFactoryForDelegate(new LocalFileHandler());
        return new FileHandlerFactoryWithCache(new FileHandlerFactoryComposite(remote, local));
    }
    
    private FileHandlerFactory awsS3FileHandlerFactory() {
        return new AwsS3FileHandlerFactory(
                this.amazonS3Client(this.awsProperties), 
                this.s3FileKeyBuilder(), 
                this.awsProperties);
    }

    @Bean public AmazonS3Client amazonS3Client(AwsProperties aws) {
        
        if(isNullOrEmpty(aws.getAccessKey()) || isNullOrEmpty(aws.getSecretKey())) {
            throw new RuntimeException("Invalid AWS properties");
        }
        
        AWSCredentials credentials = new BasicAWSCredentials(aws.getAccessKey(), aws.getSecretKey());
        
        return new AmazonS3Client(credentials); 
    }

    @Bean public AwsFileHandler.S3FileKeyBuilder s3FileKeyBuilder() {
        return new S3FileKeyBuilderImpl();
    }
    
    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
