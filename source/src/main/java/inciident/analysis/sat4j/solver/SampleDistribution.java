
package inciident.analysis.sat4j.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import inciident.clauses.LiteralList;


public class SampleDistribution extends LiteralDistribution {

    private final ArrayList<LiteralList> samples = new ArrayList<>();
    private int startIndex;

    private final byte[] model;

    public SampleDistribution(List<LiteralList> sample) {
        samples.addAll(sample);
        startIndex = 0;
        model = new byte[sample.get(0).size()];
    }

    @Override
    public void reset() {
        Arrays.fill(model, (byte) 0);
        startIndex = 0;
    }

    @Override
    public void unset(int var) {
        final int index = var - 1;
        final byte sign = model[index];
        if (sign != 0) {
            model[index] = 0;
            final int literal = sign > 0 ? var : -var;
            for (int i = 0; i < startIndex; i++) {
                if (samples.get(i).getLiterals()[index] == -literal) {
                    Collections.swap(samples, i--, --startIndex);
                }
            }
        }
    }

    @Override
    public void set(int literal) {
        final int index = Math.abs(literal) - 1;
        if (model[index] == 0) {
            model[index] = (byte) (literal > 0 ? 1 : -1);
            for (int i = startIndex; i < samples.size(); i++) {
                if (samples.get(i).getLiterals()[index] == -literal) {
                    Collections.swap(samples, i, startIndex++);
                }
            }
        }
    }

    @Override
    public int getRandomLiteral(int var) {
        if (samples.size() > (startIndex + 1)) {
            return (random.nextInt((samples.size() - startIndex) + 2) < (getPositiveCount(var - 1) + 1)) ? var : -var;
        } else {
            return random.nextBoolean() ? var : -var;
        }
    }

    public int getPositiveCount(int index) {
        int sum = 0;
        for (final LiteralList l : samples.subList(startIndex, samples.size())) {
            sum += (~l.getLiterals()[index]) >>> 31;
        }
        return sum;
    }

    public int getTotalCount() {
        return samples.size() + startIndex;
    }
}
