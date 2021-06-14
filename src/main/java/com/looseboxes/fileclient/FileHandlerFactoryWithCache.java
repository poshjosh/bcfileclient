package com.looseboxes.fileclient;

import java.util.EnumMap;
import java.util.Objects;

/**
 * @author hp
 */
public class FileHandlerFactoryWithCache implements FileHandlerFactory{
    
    private final EnumMap<FileSystemType, FileHandler> cache = new EnumMap(FileSystemType.class);
    
    private final FileHandlerFactory delegate;

    public FileHandlerFactoryWithCache(FileHandlerFactory delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }
            
    @Override
    public FileHandler get(FileSystemType fileSystemType) {
        FileHandler fileHandler = cache.get(fileSystemType);
        if(fileHandler == null) {
            fileHandler = delegate.get(fileSystemType);
            if(fileHandler != null) {
                cache.put(fileSystemType, fileHandler);
            }
        }
        return fileHandler;
    }
}
