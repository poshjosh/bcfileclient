package com.looseboxes.fileclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author hp
 */
public interface FileHandler {

    InputStream read(Path from) throws IOException;

    default void writeObject(Object obj, Path to) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024);
        Utils.serialize(obj, out);
        byte [] bytes = out.toByteArray();
        InputStream in = new ByteArrayInputStream(bytes);
        this.write(in, to, bytes.length, "application/octet-stream");
    }
    
    void write(InputStream in, Path to, long contentLength, String contentType) throws IOException;
    
    boolean delete(Path path) throws IOException;
}
