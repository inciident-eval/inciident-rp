
package inciident.clauses.solutions.analysis;

import java.util.List;

import inciident.clauses.LiteralList;

public class InteractionFinderWrapper extends InteractionFinderCombination {

    private final boolean checkResult, beIterative;

    public InteractionFinderWrapper(AInteractionFinder finder, boolean checkResult, boolean beIterative) {
        super(finder);
        this.checkResult = checkResult;
        this.beIterative = beIterative;
    }

    public List<LiteralList> find(int t) {
        if (beIterative) {
            for (int ti = 1; ti < t; ti++) {
                finder.find(ti);
            }
        }
        List<LiteralList> result = finder.find(t);
        boolean failed = checkResult && !finder.isPotentialInteraction(result);
        finder.addStatisticEntry(t, result);
        return failed ? null : result;
    }
}
