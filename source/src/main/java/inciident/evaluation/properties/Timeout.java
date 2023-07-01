
package inciident.evaluation.properties;

public class Timeout extends Property<Long> {

    public Timeout() {
        super("timeout", LongConverter);
    }
}
