package com.looseboxes.fileclient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hp
 */
public class FileHandlerComposite implements FileHandler{
    
    private static final Logger LOG = LoggerFactory.getLogger(FileHandlerComposite.class);
    
    private final FileHandler [] delegates;
    
    public FileHandlerComposite(List<FileHandler> delegates) {
        this(delegates.toArray(new FileHandler[0]));
    }
    
    public FileHandlerComposite(FileHandler... delegates) {
        this.delegates = delegates;
    }

    /**
     * Loop through our delegates and return the result of the first successful read
     * @param from The Path to read from
     * @return {@link java.io.InputStream} 
     * @throws IOException 
     */
    @Override
    public InputStream read(Path from) throws IOException {
        
        IOException exception = null;
        InputStream result = null;
        
        for(FileHandler fileHandler : delegates){
            try{
                result = fileHandler.read(from);
                if(result != null) {
                    break;
                }
            }catch(IOException e) {
                if(exception == null) {
                    exception = e;
                }else{
                    exception.addSuppressed(e);
                }
            }
        }
        
        if(result == null) {
            if(exception == null) {
                exception = new IOException();
            }
            throw exception;
        }
        
        return result;
    }

    /**
     * Call the write method on all delegates. 
     * 
     * Only the result of the first successful write determines the state. 
     * Writes by subsequent delegates may be done asynchronously.
     * 
     * @param in The {@link java.io.InputStream} containing the content to writeSilently
     * @param to The Path to writeSilently to
     * @param contentLength The content length
     * @param contentType The content type e.g image/jpeg
     * @throws IOException 
     */
    @Override
    public void write(InputStream in, Path to, long contentLength, String contentType) throws IOException {
        
        IOException exception = null;
        
        boolean oneSuccess = false;
        
        for(FileHandler fileHandler : delegates) {
        
            if( ! oneSuccess) {
          
                try{
                    
                    fileHandler.write(in, to, contentLength, contentType);
                    
                    oneSuccess = true;
                    
                }catch(IOException e) {
                    if(exception == null) {
                        exception = e;
                    }else{
                        exception.addSuppressed(e);
                    }
                }
            }else{
                
                writeSilently(fileHandler, in, to, contentLength, contentType);
            }
        }
        
        if( ! oneSuccess) {
            if(exception == null) {
                exception = new IOException();
            }
            throw exception;
        }
    }

    protected void writeSilently(FileHandler fileHandler, InputStream in, Path to, 
            long contentLength, String contentType) {
        try{
            fileHandler.write(in, to, contentLength, contentType);
        }catch(IOException e) {
            LOG.warn("Failed to write to: " + to, e);
        }
    }
    
    /**
     * Call the delete method all delegates. 
     * 
     * Only the result of the first successful delete determines the state.
     * Deletes by subsequent delegates may be done asynchronously.
     * 
     * @param path The path to deleteSilently
     * @return true if the deleteSilently was successful
     * @throws IOException 
     */
    @Override
    public boolean delete(Path path) throws IOException {
        
        IOException exception = null;
        
        boolean oneSuccess = false;
        
        boolean result = false;
        
        for(FileHandler fileHandler : delegates) {
        
            if( ! oneSuccess) {
                
                try{
                    
                    result = fileHandler.delete(path);

                    oneSuccess = true;
                    
                }catch(IOException e) {
                    if(exception == null) {
                        exception = e;
                    }else{
                        exception.addSuppressed(e);
                    }
                }
            }else{
                
                deleteSilently(fileHandler, path);
            }
        }
        
        
        if( ! oneSuccess) {
            if(exception == null) {
                exception = new IOException();
            }
            throw exception;
        }

        return result;
    }

    protected boolean deleteSilently(FileHandler fileHandler, Path path) {
        try{
            return fileHandler.delete(path);
        }catch(IOException e) {
            LOG.warn("Problem deleting: " + path, e);
            return false;
        }
    }
}
