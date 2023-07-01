
package inciident.util.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

public abstract class OutputMapper extends IOMapper<Output> {
    protected OutputMapper(Path mainPath) {
        super(mainPath);
    }

    protected OutputMapper(Map<Path, Output> ioMap, Path mainPath) {
        super(ioMap, mainPath);
    }

    protected abstract Output createOutput(Path path) throws IOException;

    public static class Stream extends OutputMapper {
        protected Stream(Map<Path, Output> ioMap, Path mainPath) {
            super(ioMap, mainPath);
        }

        public Stream(OutputStream outputStream, Charset charset) {
            super(Map.of(DEFAULT_MAIN_PATH, new Output.Stream(outputStream, charset)), DEFAULT_MAIN_PATH);
        }

        @Override
        protected Output createOutput(Path path) {
            throw new UnsupportedOperationException("cannot guess kind of requested output stream");
        }
    }

    public static class File extends OutputMapper {
        protected final Path rootPath;
        protected final Charset charset;

        public File(List<Path> paths, Path rootPath, Path mainPath, Charset charset) throws IOException {
            super(relativizeRootPath(rootPath, mainPath));
            checkParameters(paths, rootPath, mainPath);
            for (Path currentPath : paths) {
                ioMap.put(relativizeRootPath(rootPath, currentPath), new Output.File(currentPath, charset));
            }
            this.rootPath = rootPath;
            this.charset = charset;
        }

        public File(Path mainPath, Charset charset) throws IOException {
            this(List.of(mainPath), mainPath.getParent(), mainPath, charset);
        }

        @Override
        protected Output createOutput(Path path) throws IOException {
            return new Output.File(resolveRootPath(rootPath, path), charset);
        }
    }

    public static class String extends OutputMapper {
        protected final Charset charset;

        public String(Charset charset) {
            super(Map.of(DEFAULT_MAIN_PATH, new Output.String(charset)), DEFAULT_MAIN_PATH);
            this.charset = charset;
        }

        @Override
        protected Output createOutput(Path path) {
            return new Output.String(charset);
        }

        public Map<Path, java.lang.String> getOutputStrings() {
            return ioMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()
                    .getOutputStream()
                    .toString()));
        }
    }

    public static class ZIPFile extends OutputMapper {
        protected final ZipOutputStream zipOutputStream;
        protected final Charset charset;

        public ZIPFile(Path zipPath, Path mainPath, Charset charset) throws IOException {
            super(mainPath);
            this.zipOutputStream = new ZipOutputStream(new FileOutputStream(zipPath.toString()));
            this.charset = charset;
            ioMap.put(mainPath, new Output.ZIPEntry(mainPath, zipOutputStream, charset));
        }

        @Override
        protected Output createOutput(Path path) {
            return new Output.ZIPEntry(path, zipOutputStream, charset);
        }

        @Override
        public void close() throws IOException {
            super.close();
            zipOutputStream.close();
        }
    }

    public static class JARFile extends OutputMapper {
        protected final JarOutputStream jarOutputStream;
        protected final Charset charset;

        public JARFile(Path zipPath, Path mainPath, Charset charset) throws IOException {
            super(mainPath);
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            this.jarOutputStream = new JarOutputStream(new FileOutputStream(zipPath.toString()), manifest);
            this.charset = charset;
            ioMap.put(mainPath, new Output.JAREntry(mainPath, jarOutputStream, charset));
        }

        @Override
        protected Output createOutput(Path path) {
            return new Output.JAREntry(path, jarOutputStream, charset);
        }

        @Override
        public void close() throws IOException {
            super.close();
            jarOutputStream.close();
        }
    }

    public static OutputMapper of(Path mainPath, Charset charset, Options... options) throws IOException {
        return Arrays.asList(options).contains(Options.OUTPUT_FILE_JAR)
                ? new JARFile(IOObject.getPathWithExtension(mainPath, "jar"), mainPath.getFileName(), charset)
                : Arrays.asList(options).contains(Options.OUTPUT_FILE_ZIP)
                        ? new ZIPFile(IOObject.getPathWithExtension(mainPath, "zip"), mainPath.getFileName(), charset)
                        : new OutputMapper.File(mainPath, charset);
    }

    @FunctionalInterface
    public interface IORunnable {
        void run() throws IOException;
    }

    public void withMainPath(Path newMainPath, IORunnable ioRunnable) throws IOException { // todo: handle relative
        // paths / subdirs?
        create(newMainPath);
        Path oldMainPath = mainPath;
        mainPath = newMainPath;
        try {
            ioRunnable.run();
        } finally {
            mainPath = oldMainPath;
        }
    }

    public Output create(Path path) throws IOException {
        Optional<Output> outputOptional = super.get(path);
        if (outputOptional.isPresent()) return outputOptional.get();
        Output output = createOutput(path);
        ioMap.put(path, output);
        return output;
    }
}
