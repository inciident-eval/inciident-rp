
package inciident.util.io.binary;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import inciident.util.data.Result;
import inciident.util.io.InputMapper;
import inciident.util.io.OutputMapper;
import inciident.util.io.format.Format;
import inciident.util.logging.Logger;


public class SerializableObjectFormat<T> implements Format<T> {

    public static final String ID = SerializableObjectFormat.class.getCanonicalName();

    @Override
    public void write(T object, OutputMapper outputMapper) throws IOException {
        final OutputStream outputStream = outputMapper.get().getOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(object);
            oos.flush();
        } catch (final Exception e) {
            Logger.logError(e);
        }
    }

    @Override
    public Result<T> parse(InputMapper inputMapper) {
        try (ObjectInputStream in = new ObjectInputStream(inputMapper.get().getInputStream())) {
            @SuppressWarnings("unchecked")
            final T readObject = (T) in.readObject();
            return Result.of(readObject);
        } catch (final Exception e) {
            Logger.logError(e);
            return Result.empty(e);
        }
    }

    @Override
    public String getFileExtension() {
        return "serialized";
    }

    @Override
    public SerializableObjectFormat<T> getInstance() {
        return this;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public String getName() {
        return "SerializableObjectFormat";
    }
}
