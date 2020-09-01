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
public class Main {

    public static void main(String... args) {
    
        AWSCredentials credentials = new BasicAWSCredentials("<AWS_ACCESS_KEY>", "<AWS_SECRET_KEY>");
        
        AmazonS3Client client = new AmazonS3Client(credentials); 
     
        Path dir = Paths.get(System.getProperty("user.home"), "Desktop", "buzzwears_local_images_downloaded_20200831", "images");
        
        S3FileKeyBuilder s3FileKeyBuilder = (path) -> {
            String relative = dir.relativize(path).toString();
            Path s3FileKey = Paths.get("buzzwears", "public_html", "local", "images", relative);
            System.out.println("Path: " + path + ", S3FileKey: " + s3FileKey);
            return s3FileKey.toString();
        };
        
        AwsFileHandler fileHandler = new AwsFileHandler(
                "<AWS_S3_BUCKET_NAME>", client, s3FileKeyBuilder);
        
        try{
            
            AtomicInteger count = new AtomicInteger();
            
            Files.walk(dir, 10)
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
