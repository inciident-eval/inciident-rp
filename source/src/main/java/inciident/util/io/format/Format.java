
package inciident.util.io.format;

import java.io.IOException;
import java.util.function.Supplier;

import inciident.util.data.Result;
import inciident.util.extension.Extension;
import inciident.util.io.InputHeader;
import inciident.util.io.InputMapper;
import inciident.util.io.OutputMapper;


public interface Format<T> extends Extension {

    
    default Result<T> parse(InputMapper inputMapper) {
        throw new UnsupportedOperationException();
    }

    
    default Result<T> parse(InputMapper inputMapper, Supplier<T> supplier) {
        return parse(inputMapper);
    }

    
    default String serialize(T object) {
        throw new UnsupportedOperationException();
    }

    
    default void write(T object, OutputMapper outputMapper) throws IOException {
        outputMapper.get().writeText(serialize(object));
    }

    
    String getFileExtension();

    
    String getName();

    
    default Format<T> getInstance() {
        return this;
    }

    
    default boolean supportsParse() {
        return false;
    }

    
    default boolean supportsSerialize() {
        return false;
    }

    
    default boolean supportsContent(InputHeader inputHeader) {
        return supportsParse();
    }
}
