
package inciident.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import inciident.util.data.Factory;
import inciident.util.data.FactorySupplier;
import inciident.util.data.Result;
import inciident.util.io.format.Format;
import inciident.util.io.format.FormatSupplier;


public class IO {
    public static <T> Result<T> load( //
            InputStream inputStream, //
            Format<T> format //
            ) {
        return load(inputStream, format, IOObject.DEFAULT_CHARSET);
    }

    public static <T> Result<T> load( //
            InputStream inputStream, //
            Format<T> format, //
            Charset charset //
            ) {
        try (InputMapper inputMapper = new InputMapper.Stream(inputStream, charset, IOObject.EMPTY_FILE_EXTENSION)) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load(URL url, Format<T> format) {
        return load(url, format, IOObject.DEFAULT_CHARSET);
    }

    public static <T> Result<T> load(URL url, Format<T> format, Supplier<T> objectSupplier) {
        return load(url, format, objectSupplier, IOObject.DEFAULT_CHARSET);
    }

    public static <T> Result<T> load(URL url, FormatSupplier<T> formatSupplier) {
        return load(url, formatSupplier, IOObject.DEFAULT_CHARSET);
    }

    public static <T> Result<T> load(URL url, FormatSupplier<T> formatSupplier, Supplier<T> objectSupplier) {
        return load(url, formatSupplier, objectSupplier, IOObject.DEFAULT_CHARSET);
    }

    public static <T> Result<T> load( //
            URL url, //
            Format<T> format, //
            Charset charset) {
        try (InputMapper inputMapper =
                new InputMapper.Stream(url.openStream(), charset, IOObject.getFileExtension(url.getFile()))) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load( //
            URL url, //
            Format<T> format, //
            Supplier<T> factory, //
            Charset charset) {
        try (InputMapper inputMapper =
                new InputMapper.Stream(url.openStream(), charset, IOObject.getFileExtension(url.getFile()))) {
            return parse(inputMapper, format, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load( //
            URL url, //
            FormatSupplier<T> formatSupplier, //
            Supplier<T> factory, //
            Charset charset) {
        try (InputMapper inputMapper =
                new InputMapper.Stream(url.openStream(), charset, IOObject.getFileExtension(url.getFile()))) {
            return parse(inputMapper, formatSupplier, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load( //
            URL url, //
            FormatSupplier<T> formatSupplier, //
            Charset charset) {
        try (InputMapper inputMapper =
                new InputMapper.Stream(url.openStream(), charset, IOObject.getFileExtension(url.getFile()))) {
            return parse(inputMapper, formatSupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load(
            Path path,
            Format<T> format, //
            IOMapper.Options... ioMapperOptions) {
        return load(path, format, IOObject.DEFAULT_CHARSET, ioMapperOptions);
    }

    public static <T> Result<T> load(
            Path path,
            Format<T> format,
            Supplier<T> objectSupplier, //
            IOMapper.Options... ioMapperOptions) {
        return load(path, format, objectSupplier, IOObject.DEFAULT_CHARSET, ioMapperOptions);
    }

    public static <T> Result<T> load(
            Path path,
            FormatSupplier<T> formatSupplier, //
            IOMapper.Options... ioMapperOptions) {
        return load(path, formatSupplier, IOObject.DEFAULT_CHARSET, ioMapperOptions);
    }

    public static <T> Result<T> load(
            Path path,
            FormatSupplier<T> formatSupplier,
            Supplier<T> objectSupplier, //
            IOMapper.Options... ioMapperOptions) {
        return load(path, formatSupplier, objectSupplier, IOObject.DEFAULT_CHARSET, ioMapperOptions);
    }

    public static <T> Result<T> load(
            Path path,
            FormatSupplier<T> formatSupplier,
            FactorySupplier<T> factorySupplier, //
            IOMapper.Options... ioMapperOptions) {
        return load(path, formatSupplier, factorySupplier, IOObject.DEFAULT_CHARSET, ioMapperOptions);
    }

    public static <T> Result<T> load( //
            Path path, //
            Format<T> format, //
            Charset charset, //
            IOMapper.Options... ioMapperOptions) {
        try (InputMapper inputMapper = new InputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load( //
            Path path, //
            Format<T> format, //
            Supplier<T> factory, //
            Charset charset, //
            IOMapper.Options... ioMapperOptions) {
        try (InputMapper inputMapper = new InputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, format, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load( //
            Path path, //
            FormatSupplier<T> formatSupplier, //
            Supplier<T> factory, //
            Charset charset, //
            IOMapper.Options... ioMapperOptions) {
        try (InputMapper inputMapper = new InputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, formatSupplier, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load( //
            Path path, //
            FormatSupplier<T> formatSupplier, //
            Charset charset, //
            IOMapper.Options... ioMapperOptions) {
        try (InputMapper inputMapper = new InputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, formatSupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load( //
            Path path, //
            FormatSupplier<T> formatSupplier, //
            FactorySupplier<T> factorySupplier, //
            Charset charset, //
            IOMapper.Options... ioMapperOptions) {
        try (InputMapper inputMapper = new InputMapper.File(path, charset, ioMapperOptions)) {
            return parse(path, inputMapper, formatSupplier, factorySupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load(String content, Format<T> format //
            ) {
        try (InputMapper inputMapper =
                new InputMapper.String(content, IOObject.DEFAULT_CHARSET, IOObject.EMPTY_FILE_EXTENSION)) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load(String content, Format<T> format, Factory<T> factory //
            ) {
        try (InputMapper inputMapper =
                new InputMapper.String(content, IOObject.DEFAULT_CHARSET, IOObject.EMPTY_FILE_EXTENSION)) {
            return parse(inputMapper, format, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load(String content, Path path, FormatSupplier<T> formatSupplier, Factory<T> factory //
            ) {
        try (InputMapper inputMapper =
                new InputMapper.String(content, IOObject.DEFAULT_CHARSET, IOObject.getFileExtension(path))) {
            return parse(inputMapper, formatSupplier, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load(String content, Path path, FormatSupplier<T> formatSupplier //
            ) {
        try (InputMapper inputMapper =
                new InputMapper.String(content, IOObject.DEFAULT_CHARSET, IOObject.getFileExtension(path))) {
            return parse(inputMapper, formatSupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    public static <T> Result<T> load(
            String content, Path path, FormatSupplier<T> formatSupplier, FactorySupplier<T> factorySupplier //
            ) {
        try (InputMapper inputMapper =
                new InputMapper.String(content, IOObject.DEFAULT_CHARSET, IOObject.getFileExtension(path))) {
            return parse(path, inputMapper, formatSupplier, factorySupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    private static <T> Result<T> parse(InputMapper inputMapper, Format<T> format, Supplier<T> factory) {
        return format.supportsParse()
                ? format.getInstance().parse(inputMapper, factory)
                : Result.empty(new UnsupportedOperationException(format.toString()));
    }

    private static <T> Result<T> parse(InputMapper inputMapper, Format<T> format) {
        return format.supportsParse()
                ? format.getInstance().parse(inputMapper)
                : Result.empty(new UnsupportedOperationException(format.toString()));
    }

    private static <T> Result<T> parse( //
            InputMapper inputMapper, //
            FormatSupplier<T> formatSupplier, //
            Supplier<T> factory //
            ) {
        return inputMapper
                .get()
                .getInputHeader()
                .flatMap(formatSupplier::getFormat) //
                .flatMap(format -> parse(inputMapper, format, factory));
    }

    private static <T> Result<T> parse( //
            InputMapper inputMapper, //
            FormatSupplier<T> formatSupplier //
            ) {
        return inputMapper
                .get()
                .getInputHeader()
                .flatMap(formatSupplier::getFormat) //
                .flatMap(format -> parse(inputMapper, format));
    }

    private static <T> Result<T> parse( //
            Path path, //
            InputMapper inputMapper, //
            FormatSupplier<T> formatSupplier, //
            FactorySupplier<T> factorySupplier //
            ) {
        return inputMapper
                .get()
                .getInputHeader()
                .flatMap(formatSupplier::getFormat) //
                .flatMap(format -> factorySupplier
                        .getFactory(path, format) //
                        .flatMap(factory -> parse(inputMapper, format, factory)));
    }

    public static <T> void save(T object, Path path, Format<T> format, IOMapper.Options... ioMapperOptions)
            throws IOException {
        save(object, path, format, IOObject.DEFAULT_CHARSET, ioMapperOptions);
    }

    public static <T> void save(
            T object, Path path, Format<T> format, Charset charset, IOMapper.Options... ioMapperOptions)
            throws IOException {
        if (format.supportsSerialize()) {
            try (OutputMapper outputMapper = OutputMapper.of(path, charset, ioMapperOptions)) {
                format.getInstance().write(object, outputMapper);
            }
        }
    }

    public static <T> void save(T object, OutputStream outStream, Format<T> format) throws IOException {
        save(object, outStream, format, IOObject.DEFAULT_CHARSET);
    }

    public static <T> void save(T object, OutputStream outStream, Format<T> format, Charset charset)
            throws IOException {
        if (format.supportsSerialize()) {
            try (OutputMapper outputMapper = new OutputMapper.Stream(outStream, charset)) {
                format.getInstance().write(object, outputMapper);
            }
        }
    }

    public static <T> String print(T object, Format<T> format) throws IOException {
        if (format.supportsSerialize()) {
            try (OutputMapper outputMapper = new OutputMapper.String(IOObject.DEFAULT_CHARSET)) {
                format.getInstance().write(object, outputMapper);
                return outputMapper.get().getOutputStream().toString();
            }
        }
        return "";
    }

    public static <T> Map<Path, String> printHierarchy(T object, Format<T> format) throws IOException {
        if (format.supportsSerialize()) {
            try (OutputMapper.String outputMapper = new OutputMapper.String(IOObject.DEFAULT_CHARSET)) {
                format.getInstance().write(object, outputMapper);
                return outputMapper.getOutputStrings();
            }
        }
        return Collections.emptyMap();
    }

    public static <T> void write(String content, Path path, Charset charset) throws IOException {
        Files.write(
                path, //
                content.getBytes(charset), //
                StandardOpenOption.TRUNCATE_EXISTING, //
                StandardOpenOption.CREATE, //
                StandardOpenOption.WRITE);
    }

    public static <T> void write(String content, Path path) throws IOException {
        write(content, path, IOObject.DEFAULT_CHARSET);
    }
}
