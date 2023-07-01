
package inciident.util.data;


public class Identifier<T> {

    @Override
    public String toString() {
        return "Identifier [" + System.identityHashCode(this) + "]";
    }
}
