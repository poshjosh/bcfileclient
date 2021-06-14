package com.looseboxes.fileclient.config;

import com.looseboxes.fileclient.FileHandler;
import com.looseboxes.fileclient.LocalFileHandler;
import com.looseboxes.fileclient.FileHandlerFactory;
import com.looseboxes.fileclient.FileHandlerFactoryForDelegate;
import com.looseboxes.fileclient.FileHandlerFactoryWithCache;
import org.springframework.context.annotation.Bean;

/**
 * @author hp
 */
public class FileHandlerConfigurationSource {
    
    @Bean public FileHandlerFactory fileHandlerFactory() {
        return new FileHandlerFactoryWithCache(new FileHandlerFactoryForDelegate(new LocalFileHandler()));
    }
    
    @Bean(FileHandlerFactory.BASE) public FileHandler fileHandlerBase(FileHandlerFactory factory) {
        return factory.get(FileHandlerFactory.FileSystemType.BASE);
    }

    @Bean(FileHandlerFactory.IMAGES) public FileHandler fileHandlerImages(FileHandlerFactory factory) {
        return factory.get(FileHandlerFactory.FileSystemType.IMAGES);
    }
}
