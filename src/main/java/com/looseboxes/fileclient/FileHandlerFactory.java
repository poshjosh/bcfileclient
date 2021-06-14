package com.looseboxes.fileclient;

import com.looseboxes.fileclient.FileHandler;

/**
 * Produces FileHandlers for different file systems.
 * @author hp
 */
public interface FileHandlerFactory {
    
    enum FileSystemType{BASE, IMAGES}
    
    String BASE = "BASE";
    String IMAGES = "IMAGES";

    FileHandler get(FileSystemType fileSystemType);
}
