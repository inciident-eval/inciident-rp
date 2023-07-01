
package inciident.clauses.solutions.analysis;

import java.util.ArrayList;
import java.util.List;

import inciident.clauses.LiteralList;


public class RandomInteractionFinder extends AInteractionFinder {

    protected LiteralList findConfig(List<LiteralList> interactionsAll) {
        List<LiteralList> configs = new ArrayList<>(interactionsAll.size());
        configs.addAll(getRandomConfigs((int) Math.ceil(Math.log(interactionsAll.size()))));
        if (configs.isEmpty()) {
            return null;
        }
        LiteralList findBestConfig = findBestConfig(interactionsAll, configs);
        return findBestConfig != null ? findBestConfig : configs.get(0);
    }
}
