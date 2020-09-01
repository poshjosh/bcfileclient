# bcfileclient

### Client for various file systems e.g local disc, cloud (AWS etc)

### Sample code

- Maven pom.xml

```xml
    <dependencies>
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk-s3</artifactId>
            <version>1.11.735</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.0-alpha0</version>
        </dependency>
    </dependencies>
```

- Java Code

```java
package com.looseboxes.bc.fileclient;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.looseboxes.fileclient.Utils;
import com.looseboxes.fileclient.aws.AwsFileHandler;
import com.looseboxes.fileclient.aws.AwsFileHandler.S3FileKeyBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hp
 */
public class UploadDirToS3 {

    public void upload(
            String s3BucketName, String s3ObjectKeyPrefix,
            String awsAccessKey, String awsSecretKey, 
            Path dirToUploadToS3, int maxDepth) {
    
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials); 
     
        S3FileKeyBuilder s3FileKeyBuilder = (path) -> {
            
            String relative = dirToUploadToS3.relativize(path).toString();
            
            Path s3FileKey = Paths.get("s3ObjectKeyPrefix", relative);
            
            System.out.println("Path: " + path + ", S3FileKey: " + s3FileKey);
            
            return s3FileKey.toString();
        };
        
        AwsFileHandler fileHandler = new AwsFileHandler(s3BucketName, s3Client, s3FileKeyBuilder);
        
        try{
            
            AtomicInteger count = new AtomicInteger();
            
            Files.walk(dirToUploadToS3, maxDepth)
                    .forEach((path) -> {
                        if( ! Files.isDirectory(path)) {

                            final long contentLen = path.toFile().length();

                            final String ext = Utils.getExtension(path.getFileName().toString(), null);
                            
                            try(InputStream in = Files.newInputStream(path)) {
                                
                                fileHandler.write(in, path, contentLen, "image/" + ext);

                                System.out.println("SUCCESS: " + path + ", ext: " + ext + ", len: " + contentLen);

                            }catch(IOException e) {

                                System.out.println(" FAILED: " + path + ", " + e);
                                
                            }finally{
                                
                                count.incrementAndGet();
                            }
                        }
                    });
            
            System.out.println("ATTEMPTED: " + count.get());
            
        }catch(IOException e) {
            
            e.printStackTrace();
        }
    }
}
```
