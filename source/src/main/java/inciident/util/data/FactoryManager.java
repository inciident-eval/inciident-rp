
package inciident.util.data;

import inciident.util.extension.ExtensionPoint;


public abstract class FactoryManager<T> extends ExtensionPoint<Factory<T>> implements FactorySupplier<T> {

    public Result<Factory<T>> getFactoryById(String id) {
        return getExtension(id);
    }

    public abstract Result<? extends Factory<T>> getFactory(T object);
}
