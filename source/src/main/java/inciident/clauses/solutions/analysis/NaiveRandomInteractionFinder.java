
package inciident.clauses.solutions.analysis;

import java.util.List;

import inciident.clauses.LiteralList;


public class NaiveRandomInteractionFinder extends AInteractionFinder {

    @Override
    protected List<LiteralList> computePotentialInteractions(int t) {
        List<LiteralList> interactions = super.computePotentialInteractions(t);
        configurationVerificationLimit = (int) Math.ceil(10 * (Math.log(interactions.size()) / Math.log(2)) + 1);
        return interactions;
    }

    protected LiteralList findConfig(List<LiteralList> interactionsAll) {
        final List<LiteralList> randomConfigs = getRandomConfigs(1);
        return randomConfigs.isEmpty() ? null : randomConfigs.get(0);
    }
}
