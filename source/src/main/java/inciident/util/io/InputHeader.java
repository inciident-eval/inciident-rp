
package inciident.util.io;

import java.nio.charset.Charset;
import java.util.stream.Stream;


public class InputHeader {

    
    public static final int MAX_HEADER_SIZE = 0x00100000; // 1 MiB

    private final byte[] header;

    private final Charset charset;

    private final String fileExtension;

    public InputHeader(String fileExtension, byte[] header, Charset charset) {
        this.fileExtension = fileExtension;
        this.header = header;
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public byte[] getBytes() {
        return header;
    }

    public String getText() {
        return new String(header, charset);
    }

    public Stream<String> getLines() {
        return getText().lines();
    }
}
