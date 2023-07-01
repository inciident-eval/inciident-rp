
package inciident.clauses;

import java.util.Comparator;


public class ClauseLengthComparatorAsc implements Comparator<LiteralList> {

    @Override
    public int compare(LiteralList o1, LiteralList o2) {
        return o1.getLiterals().length - o2.getLiterals().length;
    }
}
