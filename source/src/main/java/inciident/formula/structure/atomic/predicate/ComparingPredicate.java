
package inciident.formula.structure.atomic.predicate;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Term;


public abstract class ComparingPredicate extends Predicate {

    public ComparingPredicate(Term leftArgument, Term rightArgument) {
        super(leftArgument, rightArgument);
    }

    protected ComparingPredicate() {
        super();
    }

    public void setArguments(Term leftArgument, Term rightArgument) {
        setChildren(Arrays.asList(leftArgument, rightArgument));
    }

    @Override
    public void setChildren(List<? extends Formula> children) {
        if (children.size() != 2) {
            throw new IllegalArgumentException("Must specify exactly two children");
        }
        final Iterator<? extends Formula> iterator = children.iterator();
        final Class<?> type1 = iterator.next().getType();
        final Class<?> type2 = iterator.next().getType();
        if (type1 != type2) {
            throw new IllegalArgumentException("Type of children differs: " + type1 + " != " + type2);
        }
        super.setChildren(children);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Boolean eval(List<?> values) {
        assert Formula.checkValues(2, values);
        assert Formula.checkValues(Comparable.class, values);
        final Comparable v1 = (Comparable) values.get(0);
        final Comparable v2 = (Comparable) values.get(1);
        return (v1 != null && v2 != null) ? compareDiff(v1.compareTo(v2)) : null;
    }

    protected abstract boolean compareDiff(int diff);
}
