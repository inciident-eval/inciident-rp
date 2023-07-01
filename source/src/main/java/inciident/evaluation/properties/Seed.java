
package inciident.evaluation.properties;

public class Seed extends Property<Long> {

    public Seed() {
        super("seed", LongConverter, System.currentTimeMillis());
    }

    public Seed(long defaultValue) {
        super("seed", LongConverter, defaultValue);
    }
}
