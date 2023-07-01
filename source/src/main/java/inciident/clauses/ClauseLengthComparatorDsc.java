
package inciident.clauses;

import java.util.Comparator;


public class ClauseLengthComparatorDsc implements Comparator<LiteralList> {

    @Override
    public int compare(LiteralList o1, LiteralList o2) {
        return o2.getLiterals().length - o1.getLiterals().length;
    }
}
