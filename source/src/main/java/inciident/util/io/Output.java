
package inciident.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public abstract class Output implements IOObject {
    protected final OutputStream outputStream;
    protected final Charset charset;

    protected Output(OutputStream outputStream, Charset charset) {
        Objects.requireNonNull(outputStream);
        Objects.requireNonNull(charset);
        this.outputStream = outputStream;
        this.charset = charset;
    }

    public static class Stream extends Output {
        public Stream(OutputStream outputStream, Charset charset) {
            super(outputStream, charset);
        }
    }

    public static class File extends Output {
        public File(Path path, Charset charset) throws IOException {
            super(createOutputStream(path), charset);
        }

        private static OutputStream createOutputStream(Path path) throws IOException {
            if (path.getParent() != null) path.getParent().toFile().mkdirs();
            return Files.newOutputStream(
                    path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
    }

    public static class String extends Output {
        public String(Charset charset) {
            super(new ByteArrayOutputStream(), charset);
        }
    }

    public static class ZIPEntry extends Output {
        protected final Path path;

        public ZIPEntry(Path path, ZipOutputStream zipOutputStream, Charset charset) {
            super(zipOutputStream, charset);
            this.path = path;
        }

        @Override
        public void writeText(java.lang.String text) throws IOException {
            ZipEntry zipEntry = new ZipEntry(path.toString());
            ((ZipOutputStream) outputStream).putNextEntry(zipEntry);
            super.writeText(text);
            ((ZipOutputStream) outputStream).closeEntry();
        }
    }

    public static class JAREntry extends Output {
        protected final Path path;

        public JAREntry(Path path, JarOutputStream jarOutputStream, Charset charset) {
            super(jarOutputStream, charset);
            this.path = path;
        }

        @Override
        public void writeText(java.lang.String text) throws IOException {
            JarEntry jarEntry = new JarEntry(path.toString());
            ((JarOutputStream) outputStream).putNextEntry(jarEntry);
            super.writeText(text);
            ((JarOutputStream) outputStream).closeEntry();
        }
    }

    public Charset getCharset() {
        return charset;
    }

    public void writeText(java.lang.String text) throws IOException {
        outputStream.write(text.getBytes(charset));
        outputStream.flush();
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
