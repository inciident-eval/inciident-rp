
package inciident.util.logging;


@FunctionalInterface
public interface Formatter {

    void format(StringBuilder message);
}
