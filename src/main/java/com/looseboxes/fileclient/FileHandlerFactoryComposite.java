package com.looseboxes.fileclient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public class FileHandlerFactoryComposite implements FileHandlerFactory{
    
    private final FileHandlerFactory [] delegates;
    
    public FileHandlerFactoryComposite(List<FileHandlerFactory> delegates) {
        this(delegates.toArray(new FileHandlerFactory[0]));
    }

    public FileHandlerFactoryComposite(FileHandlerFactory... delegates) {
        this.delegates = Objects.requireNonNull(delegates);
    }

    @Override
    public FileHandler get(FileSystemType fileSystemType) {
        
        List<FileHandler> fileHandlers = Arrays.asList(delegates).stream()
                .map((delegate) -> delegate.get(fileSystemType)).collect(Collectors.toList());
        
        return new FileHandlerCompositeAsyncBackups(fileHandlers);
    }
}
