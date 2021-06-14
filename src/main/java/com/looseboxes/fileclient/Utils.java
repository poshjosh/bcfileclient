package com.looseboxes.fileclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * @author hp
 */
public class Utils {
    
    
    /** 
     * @param path The path whose extension in form 'jpg' will be returned
     * @param resultIfNone The result to return if the path contains no extension
     * @return The extension of the input path in form 'jpg'
     */
    public static String getExtension(String path, String resultIfNone) {
        int n = path.lastIndexOf('.');
        return n == -1 ? resultIfNone : path.substring(n + 1);
    }
    
    /**
     * Serializes the given object value to an output stream, and close the output stream.
     *
     * @param value object value to serialize
     * @param outputStream output stream to serialize into
     * @throws java.io.IOException
     */
    public static void serialize(Object value, OutputStream outputStream) throws IOException {
        try {
            new ObjectOutputStream(outputStream).writeObject(value);
        } finally {
            outputStream.close();
        }
    }

    /**
     * Deserializes the given input stream into to a newly allocated object, 
     * and close the input stream.
     *
     * @param <S> The type of the object that will be deserialized
     * @param inputStream input stream to deserialize
     * @return The object deserialized from the Inputstream
     */
    @SuppressWarnings("unchecked")
    public static <S extends Serializable> S deserialize(InputStream inputStream) throws IOException {
        try {
            return (S) new ObjectInputStream(inputStream).readObject();
        } catch (ClassNotFoundException exception) {
            throw new IOException("Failed to deserialize object", exception);
        } finally {
            inputStream.close();
        }
    }
}
