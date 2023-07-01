
package inciident.evaluation.interactionfinder;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import inciident.clauses.LiteralList;
import inciident.clauses.solutions.analysis.ConfigurationVerifyer;

public class ConfigurationOracle implements ConfigurationVerifyer {
    private final List<LiteralList> interactions;
    private final double fpNoise, fnNoise;

    public ConfigurationOracle(List<LiteralList> interactions, double fpNoise, double fnNoise) {
        this.interactions = interactions;
        this.fpNoise = fpNoise;
        this.fnNoise = fnNoise;
    }

    @Override
    public int test(LiteralList configuration) {
        final Random random = new Random(Arrays.hashCode(configuration.getLiterals()));

        int error = 1;
        for (LiteralList interaction : interactions) {
            final boolean isFailing = configuration.containsAll(interaction);
            if (isFailing) {
                break;
            }
            error++;
        }
        error %= interactions.size() + 1;

        return error == 0 //
                ? random.nextDouble() < fnNoise //
                        ? random.nextInt(interactions.size()) + 1 //
                        : 0 //
                : random.nextDouble() < fpNoise //
                        ? 0 //
                        : error;
    }
}
