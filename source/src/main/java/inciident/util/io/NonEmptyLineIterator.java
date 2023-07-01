
package inciident.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Supplier;


public class NonEmptyLineIterator implements Supplier<String> {

    private final BufferedReader reader;
    private String line = null;
    private int lineCount = 0;

    public NonEmptyLineIterator(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public String get() {
        try {
            do {
                line = reader.readLine();
                if (line == null) {
                    return null;
                }
                lineCount++;
            } while (line.trim().isEmpty());
            return line;
        } catch (final IOException e) {
            return null;
        }
    }

    public String currentLine() {
        return line;
    }

    public void setCurrentLine(String line) {
        this.line = line;
    }

    public int getLineCount() {
        return lineCount;
    }
}
