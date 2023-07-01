
package inciident.analysis.solver;

import inciident.formula.structure.atomic.Assignment;
import inciident.formula.structure.atomic.literal.VariableMap;


public interface Solver extends Cloneable {

    Assignment getAssumptions();

    DynamicFormula<?> getDynamicFormula();

    VariableMap getVariables();

    default void reset() {}
}
