
package inciident.util.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import inciident.util.data.Result;


public abstract class ExtensionPoint<T extends Extension> {

    public static class NoSuchExtensionException extends Exception {

        private static final long serialVersionUID = -8143277745205866068L;

        public NoSuchExtensionException(String message) {
            super(message);
        }
    }

    private final HashMap<String, Integer> indexMap = new HashMap<>();
    private final List<T> extensions = new CopyOnWriteArrayList<>();

    
    public synchronized boolean addExtension(T extension) {
        if ((extension != null) && !indexMap.containsKey(extension.getIdentifier()) && extension.initialize()) {
            indexMap.put(extension.getIdentifier(), extensions.size());
            extensions.add(extension);
            return true;
        }
        return false;
    }

    
    public synchronized List<T> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    
    public Result<T> getExtension(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null!");
        final Integer index = indexMap.get(identifier);
        return index != null
                ? Result.of(extensions.get(index))
                : Result.empty(new NoSuchExtensionException("No extension found for identifier " + identifier));
    }
}
