package com.looseboxes.fileclient.aws;

import com.looseboxes.fileclient.aws.AwsFileHandler.S3FileKeyBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author hp
 */
public class S3FileKeyBuilderImpl implements S3FileKeyBuilder{

    @Override
    public String from(Path path) {
        final Path userHome = Paths.get(System.getProperty("user.home"));
        // Paths having \\ separator will not form a folder structure in AWS S3
        // Windows systems generate paths having \\ separator
        // We replace all \\ with /
        final String s3FileKey = userHome.relativize(path).toString().replace('\\', '/');
        return s3FileKey;
    }
}
