
package inciident.evaluation.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;

import inciident.util.data.Result;
import inciident.util.io.IO;
import inciident.util.io.format.FormatSupplier;
import inciident.util.logging.Logger;


public class ModelReader<T> {

    private String defaultFileName = "model.xml";
    private Path pathToFiles;
    private FormatSupplier<T> formatSupplier;

    public final Result<T> read(final String name) {
        Result<T> fm = null;

        fm = readFromFolder(pathToFiles, name);
        if (fm.isPresent()) {
            return fm;
        }

        fm = readFromFile(pathToFiles, name);
        if (fm.isPresent()) {
            return fm;
        }

        fm = readFromZip(pathToFiles, name);

        return fm;
    }

    public Path getPathToFiles() {
        return pathToFiles;
    }

    public void setPathToFiles(Path pathToFiles) {
        this.pathToFiles = pathToFiles;
    }

    public String getDefaultFileName() {
        return defaultFileName;
    }

    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName = defaultFileName;
    }

    public FormatSupplier<T> getFormatSupplier() {
        return formatSupplier;
    }

    public void setFormatSupplier(FormatSupplier<T> formatSupplier) {
        this.formatSupplier = formatSupplier;
    }

    public Result<T> loadFile(final Path path) {
        return IO.load(path, formatSupplier);
    }

    public Result<T> readFromFolder(final Path rootPath, final String name) {
        final Path modelFolder = rootPath.resolve(name);
        Logger.logDebug("Trying to load from folder " + modelFolder);
        if (Files.exists(modelFolder) && Files.isDirectory(modelFolder)) {
            final Path path = modelFolder.resolve(defaultFileName);
            if (Files.exists(path)) {
                return loadFile(path);
            } else {
                return readFromFile(modelFolder, "model");
            }
        } else {
            return Result.empty();
        }
    }

    public Result<T> readFromFile(final Path rootPath, final String name) {
        Logger.logDebug("Trying to load from file " + name);
        Result<T> loadedFm = loadFile(rootPath.resolve(name));
        if (loadedFm.isPresent()) {
            return loadedFm;
        } else {
            Logger.logDebug(loadedFm.getProblems().get(0));
        }
        final Filter<Path> fileFilter = file -> Files.isReadable(file)
                && Files.isRegularFile(file)
                && file.getFileName().toString().matches("^" + name + "\\.\\w+$");
        try (DirectoryStream<Path> files = Files.newDirectoryStream(rootPath, fileFilter)) {
            final Iterator<Path> iterator = files.iterator();
            while (iterator.hasNext()) {
                final Path next = iterator.next();
                Logger.logDebug("Trying to load from file " + next);
                loadedFm = loadFile(next);
                if (loadedFm.isPresent()) {
                    return loadedFm;
                }
            }
            return Result.empty();
        } catch (final IOException e) {
            Logger.logError(e);
        }
        return Result.empty();
    }

    protected Result<T> readFromZip(final Path rootPath, final String name) {
        final Filter<Path> fileFilter = file -> Files.isReadable(file)
                && Files.isRegularFile(file)
                && file.getFileName().toString().matches(".*[.]zip\\Z");
        try (DirectoryStream<Path> files = Files.newDirectoryStream(rootPath, fileFilter)) {
            for (final Path path : files) {
                Logger.logDebug("Trying to load from zip file " + path);
                final URI uri = URI.create("jar:" + path.toUri().toString());
                try (final FileSystem zipFs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
                    for (final Path root : zipFs.getRootDirectories()) {
                        Result<T> fm = readFromFolder(root, name);
                        if (fm.isPresent()) {
                            return fm;
                        }
                        fm = readFromFile(root, name);
                        if (fm.isPresent()) {
                            return fm;
                        }
                    }
                } catch (final IOException e) {
                    Logger.logError(e);
                }
            }
        } catch (final IOException e) {
            Logger.logError(e);
        }
        return Result.empty();
    }

    public void dispose() {
        Logger.uninstall();
    }
}
