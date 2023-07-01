
package inciident.util.io.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import inciident.util.io.format.Format;

public abstract class BinaryFormat<T> implements Format<T> {

    protected void writeBytes(OutputStream out, byte[] bytes) throws IOException {
        out.write(bytes);
    }

    protected void writeString(OutputStream out, String string) throws IOException {
        final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        writeInt(out, bytes.length);
        writeBytes(out, bytes);
    }

    protected void writeInt(OutputStream out, int value) throws IOException {
        final byte[] integerBytes = new byte[Integer.BYTES];
        integerBytes[0] = (byte) ((value >>> 24) & 0xff);
        integerBytes[1] = (byte) ((value >>> 16) & 0xff);
        integerBytes[2] = (byte) ((value >>> 8) & 0xff);
        integerBytes[3] = (byte) (value & 0xff);
        out.write(integerBytes);
    }

    protected void writeByte(OutputStream out, byte value) throws IOException {
        out.write(value);
    }

    protected void writeBool(OutputStream out, boolean value) throws IOException {
        out.write((byte) (value ? 1 : 0));
    }

    protected byte[] readBytes(InputStream in, int size) throws IOException {
        final byte[] bytes = new byte[size];
        final int byteCount = in.read(bytes, 0, bytes.length);
        if (byteCount != bytes.length) {
            throw new IOException();
        }
        return bytes;
    }

    protected String readString(InputStream in) throws IOException {
        return new String(readBytes(in, readInt(in)), StandardCharsets.UTF_8);
    }

    protected int readInt(InputStream in) throws IOException {
        final byte[] integerBytes = new byte[Integer.BYTES];
        final int byteCount = in.read(integerBytes, 0, integerBytes.length);
        if (byteCount != integerBytes.length) {
            throw new IOException();
        }
        return ((integerBytes[0] & 0xff) << 24)
                | ((integerBytes[1] & 0xff) << 16)
                | ((integerBytes[2] & 0xff) << 8)
                | ((integerBytes[3] & 0xff));
    }

    protected byte readByte(InputStream in) throws IOException {
        final int readByte = in.read();
        if (readByte < 0) {
            throw new IOException();
        }
        return (byte) readByte;
    }

    protected boolean readBool(InputStream in) throws IOException {
        final int boolByte = in.read();
        if (boolByte < 0) {
            throw new IOException();
        }
        return boolByte == 1;
    }
}
