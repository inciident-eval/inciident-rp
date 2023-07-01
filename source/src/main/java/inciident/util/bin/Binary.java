
package inciident.util.bin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import inciident.util.extension.Extension;


public abstract class Binary implements Extension {
    public static final Path BINARY_DIRECTORY = Paths.get(OperatingSystem.HOME_DIRECTORY, ".featjar-bin");

    public Binary() {
        extractResources();
    }

    public abstract Set<String> getResourceNames();

    public Path getPath() {
        return null;
    }

    public void extractResources() {
        BINARY_DIRECTORY.toFile().mkdirs();
        for (String resourceName : getResourceNames()) {
            try {
                Path outputPath = BINARY_DIRECTORY.resolve(resourceName);
                if (Files.notExists(outputPath)) {
                    JAR.extractResource("bin/" + resourceName, outputPath);
                    outputPath.toFile().setExecutable(true);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
