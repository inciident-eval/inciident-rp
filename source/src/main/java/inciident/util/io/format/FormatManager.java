
package inciident.util.io.format;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import inciident.util.data.Result;
import inciident.util.extension.ExtensionPoint;
import inciident.util.io.IOObject;
import inciident.util.io.InputHeader;


public class FormatManager<T> extends ExtensionPoint<Format<T>> implements FormatSupplier<T> {

    public Result<Format<T>> getFormatById(String id) {
        return getExtension(id);
    }

    public List<Format<T>> getFormatListForExtension(Path path) {
        if (path == null) {
            return Collections.emptyList();
        }
        return getFormatList(IOObject.getFileExtension(path));
    }

    @Override
    public Result<Format<T>> getFormat(InputHeader inputHeader) {
        final List<Format<T>> extensions = getExtensions();
        return extensions.stream()
                .filter(format -> Objects.equals(inputHeader.getFileExtension(), format.getFileExtension()))
                .filter(format -> format.supportsContent(inputHeader))
                .findFirst()
                .map(Result::of)
                .orElseGet(() ->
                        Result.empty(new NoSuchExtensionException("No suitable format found for file extension \"."
                                + inputHeader.getFileExtension() + "\". Possible Formats: " + getExtensions())));
    }

    private List<Format<T>> getFormatList(final String fileExtension) {
        return getExtensions().stream()
                .filter(Format::supportsParse)
                .filter(format -> fileExtension.equals(format.getFileExtension()))
                .collect(Collectors.toList());
    }
}
