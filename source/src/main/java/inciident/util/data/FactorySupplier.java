
package inciident.util.data;

import java.nio.file.Path;

import inciident.util.io.format.Format;


@FunctionalInterface
public interface FactorySupplier<T> {

    static <T> FactorySupplier<T> of(Factory<T> factory) {
        return (path, format) -> Result.of(factory);
    }

    
    Result<Factory<T>> getFactory(Path path, Format<T> format);
}
