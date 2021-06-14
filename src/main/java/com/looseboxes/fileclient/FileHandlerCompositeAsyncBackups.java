package com.looseboxes.fileclient;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import org.springframework.scheduling.annotation.Async;

/**
 * @author hp
 */
public class FileHandlerCompositeAsyncBackups extends FileHandlerComposite{

    public FileHandlerCompositeAsyncBackups(List<FileHandler> delegates) {
        super(delegates);
    }

    public FileHandlerCompositeAsyncBackups(FileHandler... delegates) {
        super(delegates);
    }

    @Async
    @Override
    protected boolean deleteSilently(FileHandler fileHandler, Path path) {
        return super.deleteSilently(fileHandler, path);
    }

    @Async
    @Override
    protected void writeSilently(FileHandler fileHandler, InputStream in, Path to, long contentLength, String contentType) {
        super.writeSilently(fileHandler, in, to, contentLength, contentType); 
    }
}
