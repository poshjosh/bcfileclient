package com.looseboxes.fileclient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hp
 */
public class LocalFileHandler implements FileHandler{
    
    private final Logger log = LoggerFactory.getLogger(LocalFileHandler.class);

    @Override
    public InputStream read(Path from) throws IOException {
        
        log.debug("Reading from local file: {}", from);
        
        return Files.newInputStream(from);
    }

    @Override
    public void write(InputStream in, Path to, long contentLength, String contentType) throws IOException {
        log.debug("Writing to local file: {}, content length: {}, type: {}", to, contentLength, contentType);
        Files.copy(in, to);
    }

    @Override
    public boolean delete(Path path) throws IOException {
        
        boolean deleted = false;
        
        if(Files.exists(path)) {
            
            try{
                
                deleted = Files.deleteIfExists(path);

                if( ! deleted) {

                    log.info("Will delete on exit: {}", path);

                    path.toFile().deleteOnExit();
                }
            }catch(IOException e) {
                
                log.warn("Problem deleting: " + path, e);
                
                log.info("Will delete on exit: {}", path);
                
                path.toFile().deleteOnExit();
                
                throw e;
            }
        }
        
        return deleted;
    }
    
}
