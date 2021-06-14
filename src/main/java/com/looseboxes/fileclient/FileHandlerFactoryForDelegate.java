package com.looseboxes.fileclient;

import java.util.Objects;

/**
 * @author hp
 */
public class FileHandlerFactoryForDelegate implements FileHandlerFactory{
    
    private final FileHandler delegate;

    public FileHandlerFactoryForDelegate(FileHandler delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public FileHandler get(FileSystemType fileSystemType) {
        return delegate;
    }
}
