
package inciident.clauses;

import java.util.Comparator;
import java.util.List;


public class ClauseListLengthComparatorAsc implements Comparator<List<LiteralList>> {

    @Override
    public int compare(List<LiteralList> o1, List<LiteralList> o2) {
        return addLengths(o1) - addLengths(o2);
    }

    protected int addLengths(List<LiteralList> o) {
        int count = 0;
        for (final LiteralList literalSet : o) {
            count += literalSet.getLiterals().length;
        }
        return count;
    }
}
