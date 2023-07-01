
package inciident.util.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;


public interface IOObject extends AutoCloseable {
    java.lang.String EMPTY_FILE_EXTENSION = "";
    Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    void close() throws IOException;

    
    static String getFileNameWithoutExtension(Path path) {
        return getFileNameWithoutExtension(path.getFileName().toString());
    }

    
    static String getFileNameWithoutExtension(String fileName) {
        final int extensionIndex = fileName.lastIndexOf('.');
        return (extensionIndex > 0) ? fileName.substring(0, extensionIndex) : fileName;
    }

    
    static String getFileExtension(Path path) {
        if (path == null) {
            return EMPTY_FILE_EXTENSION;
        }
        return getFileExtension(path.getFileName().toString());
    }

    
    static String getFileExtension(String fileName) {
        if (fileName == null) {
            return EMPTY_FILE_EXTENSION;
        }
        final int extensionIndex = fileName.lastIndexOf('.');
        return (extensionIndex > 0) ? fileName.substring(extensionIndex + 1) : "";
    }

    static String getFileNameWithExtension(String fileName, String fileExtension) {
        if (fileExtension == null || fileExtension.equals(EMPTY_FILE_EXTENSION))
            return IOObject.getFileNameWithoutExtension(fileName);
        return String.format("%s.%s", IOObject.getFileNameWithoutExtension(fileName), fileExtension);
    }

    static Path getPathWithExtension(Path path, String fileExtension) {
        return path.resolveSibling(getFileNameWithExtension(path.getFileName().toString(), fileExtension));
    }

    static Path getPathWithExtension(String fileName, String fileExtension) {
        return Paths.get(getFileNameWithExtension(fileName, fileExtension));
    }
}
