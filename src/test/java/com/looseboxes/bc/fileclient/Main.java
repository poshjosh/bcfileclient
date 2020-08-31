package com.looseboxes.bc.fileclient;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author hp
 */
public class Main {

    public static void main(String... args) {
    
        AWSCredentials credentials = new BasicAWSCredentials("AKIA3O55AVCBUGAX6HQ3", 
                "vS+1KRRtv63H24DfB9rvsQkej0FEwsxilPLTwpDo");
        
        AmazonS3Client client = new AmazonS3Client(credentials); 
     
        AwsFileHandler fileHandler = new AwsFileHandler(
                "buzzwears-storage", client, (path) -> path.toString());
        
        //C:\Users\hp\Desktop\buzzwears_local_images_downloaded_20200831\images
        Path dir = Paths.get(System.getProperty("user.home"), "Desktop", "buzzwears_local_images_downloaded_20200831", "images");

        try{
            
            Files.walk(dir, 10)
                    .forEach((path) -> {
                        if( ! Files.isDirectory(path)) {

                            final long contentLen = path.toFile().length();

                            final String ext = Objects.requireNonNull(Utils.getExtension(path.getFileName().toString(), null));

                            try(InputStream in = Files.newInputStream(path)) {
                                
                                String relative = dir.relativize(path).toString();
//                                System.out.println("RELATIVE " + relative);
                                
                                Path target = Paths.get("buzzwears", "public_html", "local", "images", relative);

                                fileHandler.write(in, target, contentLen, "image/" + ext);

                                System.out.println("SUCCESS: " + target + ", ext: " + ext + ", len: " + contentLen);

                            }catch(IOException e) {

                                System.out.println(" FAILED: " + path + ", " + e);
                            }
                        }
                    });
        }catch(IOException e) {
            e.printStackTrace();
        }
        
    }
}
