
package inciident.util.data;

import java.util.function.Supplier;

import inciident.util.extension.Extension;


public interface Factory<T> extends Extension, Supplier<T> {}
