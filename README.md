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
    
    public static void main(String... args) {
        String bucketName = "???";
        String accessKey = "???";
        String secretKey = "???";
        Path base = Paths.get(System.getProperty("user.home"));
        Path context = Paths.get(".webstore", "config");
        Path dirToUpload = base.resolve(context);
        Path s3FileKeyPrefix = context;
        int maxDepth = 7;
        new UploadDirToS3().upload(bucketName, accessKey, secretKey, dirToUpload, s3FileKeyPrefix, maxDepth);
    }
    
    public void upload(
            String s3BucketName, String awsAccessKey, String awsSecretKey, 
            Path dirToUploadToS3, Path s3FileKeyPrefix, int maxDepth) {
    
        AWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials); 
     
        AwsFileHandler.S3FileKeyBuilder s3FileKeyBuilder = (path) -> {
            
            Path relativePath = dirToUploadToS3.relativize(path);
            
            String relative = s3FileKeyPrefix == null ? relativePath.toString() :
                    s3FileKeyPrefix.resolve(relativePath).toString();
            
            String s3FileKey = relative.replace('\\', '/');
            
            System.out.println("Path: " + path + ", S3FileKey: " + s3FileKey);
            
            return s3FileKey;
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

### Using the AWS S3 Client directly

```java
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
```

