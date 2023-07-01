
package inciident.util.extension;


public interface Extension {

    
    default String getIdentifier() {
        return getClass().getCanonicalName();
    }

    
    default boolean initialize() {
        return true;
    }
}
