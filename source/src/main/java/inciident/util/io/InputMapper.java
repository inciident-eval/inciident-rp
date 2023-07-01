
package inciident.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import inciident.util.data.Problem;
import inciident.util.data.Result;

public abstract class InputMapper extends IOMapper<Input> {
    protected InputMapper(Path mainPath) {
        super(mainPath);
    }

    protected InputMapper(Map<Path, Input> ioMap, Path mainPath) {
        super(ioMap, mainPath);
    }

    public static class Stream extends InputMapper {
        public Stream(
                Map<Path, InputStream> pathInputStreamMap,
                Path rootPath,
                Path mainPath,
                Charset charset,
                java.lang.String fileExtension) {
            super(relativizeRootPath(rootPath, mainPath));
            checkParameters(pathInputStreamMap.keySet(), rootPath, mainPath);
            for (Path currentPath : pathInputStreamMap.keySet()) {
                ioMap.put(
                        relativizeRootPath(rootPath, currentPath),
                        new Input.Stream(pathInputStreamMap.get(currentPath), charset, fileExtension));
            }
        }

        public Stream(InputStream inputStream, Charset charset, java.lang.String fileExtension) {
            this(Map.of(DEFAULT_MAIN_PATH, inputStream), null, DEFAULT_MAIN_PATH, charset, fileExtension);
        }
    }

    public static class File extends InputMapper {
        public File(List<Path> paths, Path rootPath, Path mainPath, Charset charset) throws IOException {
            super(relativizeRootPath(rootPath, mainPath));
            checkParameters(paths, rootPath, mainPath);
            for (Path currentPath : paths) {
                ioMap.put(relativizeRootPath(rootPath, currentPath), new Input.File(currentPath, charset));
            }
        }

        public File(Path mainPath, Charset charset, Options... options) throws IOException {
            this(
                    Arrays.asList(options).contains(Options.INPUT_FILE_HIERARCHY)
                            ? getFilePathsInDirectory(mainPath.getParent())
                            : List.of(mainPath),
                    mainPath.getParent(),
                    mainPath,
                    charset);
        }
    }

    public static class String extends InputMapper {
        public String(
                Map<Path, java.lang.String> pathStringMap,
                Path rootPath,
                Path mainPath,
                Charset charset,
                java.lang.String fileExtension) {
            super(relativizeRootPath(rootPath, mainPath));
            checkParameters(pathStringMap.keySet(), rootPath, mainPath);
            for (Path currentPath : pathStringMap.keySet()) {
                ioMap.put(
                        relativizeRootPath(rootPath, currentPath),
                        new Input.String(pathStringMap.get(currentPath), charset, fileExtension));
            }
        }

        public String(java.lang.String text, Charset charset, java.lang.String fileExtension) {
            this(Map.of(DEFAULT_MAIN_PATH, text), null, DEFAULT_MAIN_PATH, charset, fileExtension);
        }
    }

    public <T> Result<T> withMainPath(Path newMainPath, Supplier<Result<T>> supplier) { // todo: handle relative paths /
        // subdirs?
        if (ioMap.get(newMainPath) == null)
            return Result.empty(new Problem("could not find main path " + mainPath, Problem.Severity.WARNING));
        Path oldMainPath = mainPath;
        mainPath = newMainPath;
        Result<T> result = supplier.get();
        mainPath = oldMainPath;
        return result;
    }
}
