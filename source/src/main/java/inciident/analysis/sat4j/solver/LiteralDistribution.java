
package inciident.analysis.sat4j.solver;

import java.util.Random;


public abstract class LiteralDistribution {

    protected Random random = new Random(0);

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public abstract void reset();

    public abstract void set(int literal);

    public abstract void unset(int var);

    public abstract int getRandomLiteral(int var);
}
