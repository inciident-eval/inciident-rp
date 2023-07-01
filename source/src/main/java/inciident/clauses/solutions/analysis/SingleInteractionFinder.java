
package inciident.clauses.solutions.analysis;

import java.util.List;

import inciident.clauses.LiteralList;


public class SingleInteractionFinder extends AInteractionFinder {

    protected LiteralList findConfig(List<LiteralList> interactions) {
        final int limit = (int) Math.ceil(2 * (Math.log(interactions.size()) / Math.log(2)) + 1);
        LiteralList bestConfig = findBestConfig(interactions, getRandomConfigs(limit));

        return bestConfig != null ? bestConfig : getAtLeastOneConfigs(interactions);
    }

    private LiteralList getAtLeastOneConfigs(List<LiteralList> interactionsAll) {
        return choose(interactionsAll);
    }
}
