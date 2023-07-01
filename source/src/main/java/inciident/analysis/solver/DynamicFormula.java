
package inciident.analysis.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.VariableMap;


public interface DynamicFormula<O> {

    List<O> getConstraints();

    VariableMap getVariableMap();

    
    List<O> push(Formula clause);

    
    default List<O> push(Collection<Formula> clauses) {
        int addCount = 0;
        final ArrayList<O> constraintList = new ArrayList<>(clauses.size());
        for (final Formula clause : clauses) {
            try {
                push(clause);
                addCount++;
            } catch (final Exception e) {
                pop(addCount);
                throw e;
            }
        }
        return constraintList;
    }

    O peek();

    
    O pop();

    
    default void pop(int count) {
        if (count > size()) {
            count = size();
        }
        for (int i = 0; i < count; i++) {
            pop();
        }
    }

    default void clear() {
        pop(size());
    }

    int size();

    
    void remove(O constraint);
}
