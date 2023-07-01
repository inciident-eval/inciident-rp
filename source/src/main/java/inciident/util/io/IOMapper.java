
package inciident.util.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class IOMapper<T extends IOObject> implements AutoCloseable, Supplier<T> {
    public enum Options {
        
        INPUT_FILE_HIERARCHY,
        
        OUTPUT_FILE_ZIP,
        
        OUTPUT_FILE_JAR
    }

    protected static final Path DEFAULT_MAIN_PATH = Paths.get("__main__");
    protected final Map<Path, T> ioMap = new HashMap<>();
    protected Path mainPath;

    protected IOMapper(Path mainPath) {
        Objects.requireNonNull(mainPath);
        this.mainPath = mainPath;
    }

    protected IOMapper(Map<Path, T> ioMap, Path mainPath) {
        this(mainPath);
        Objects.requireNonNull(ioMap);
        if (ioMap.get(mainPath) == null) throw new IllegalArgumentException("could not find main path " + mainPath);
        this.ioMap.putAll(ioMap);
    }

    protected static List<Path> getFilePathsInDirectory(Path rootPath) throws IOException {
        List<Path> paths;
        rootPath = rootPath != null ? rootPath : Paths.get("");
        try (Stream<Path> walk = Files.walk(rootPath)) {
            paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        }
        return paths;
    }

    protected static Path relativizeRootPath(Path rootPath, Path currentPath) {
        return rootPath != null ? rootPath.relativize(currentPath) : currentPath;
    }

    protected static Path resolveRootPath(Path rootPath, Path currentPath) {
        return rootPath != null ? rootPath.resolve(currentPath) : currentPath;
    }

    protected static void checkParameters(Collection<Path> paths, Path rootPath, Path mainPath) {
        if (rootPath != null && paths.stream().anyMatch(path -> !path.startsWith(rootPath))) {
            throw new IllegalArgumentException("all paths must start with the root path");
        } else if (!paths.contains(mainPath)) {
            throw new IllegalArgumentException("main path must be included");
        }
    }

    @Override
    public T get() {
        return ioMap.get(mainPath);
    }

    public Optional<T> get(Path path) {
        return Optional.ofNullable(ioMap.get(path));
    }

    public Optional<Path> getPath(T ioObject) {
        return ioMap.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), ioObject))
                .findAny()
                .map(Map.Entry::getKey);
    }

    public Optional<Path> resolve(T sibling, Path path) {
        return getPath(sibling).map(_path -> _path.resolveSibling(path));
    }

    public Optional<Path> resolve(Path path) {
        return resolve(get(), path);
    }

    @Override
    public void close() throws IOException {
        for (T ioObject : ioMap.values()) {
            ioObject.close();
        }
    }
}
