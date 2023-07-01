
package inciident.util.io.format;

import inciident.util.data.Result;
import inciident.util.io.InputHeader;


@FunctionalInterface
public interface FormatSupplier<T> {

    static <T> FormatSupplier<T> of(Format<T> format) {
        return inputHeader -> Result.of(format);
    }

    
    Result<Format<T>> getFormat(InputHeader inputHeader);
}
