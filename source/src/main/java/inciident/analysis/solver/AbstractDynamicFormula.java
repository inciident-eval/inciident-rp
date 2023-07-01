
package inciident.analysis.solver;

import java.util.ArrayList;
import java.util.List;

import inciident.formula.structure.atomic.literal.VariableMap;


public abstract class AbstractDynamicFormula<O> implements DynamicFormula<O> {

    protected final ArrayList<O> constraints;
    protected final VariableMap variableMap;

    public AbstractDynamicFormula(VariableMap variableMap) {
        this.variableMap = variableMap;
        constraints = new ArrayList<>();
    }

    protected AbstractDynamicFormula(AbstractDynamicFormula<O> oldFormula) {
        variableMap = oldFormula.variableMap;
        constraints = new ArrayList<>(oldFormula.constraints);
    }

    @Override
    public List<O> getConstraints() {
        return constraints;
    }

    @Override
    public VariableMap getVariableMap() {
        return variableMap;
    }

    @Override
    public O pop() {
        return removeConstraint(constraints.size() - 1);
    }

    @Override
    public void remove(O constraint) {
        removeConstraint(constraints.indexOf(constraint));
    }

    protected O removeConstraint(final int index) {
        return constraints.remove(index);
    }

    @Override
    public int size() {
        return constraints.size();
    }

    @Override
    public O peek() {
        return constraints.get(constraints.size() - 1);
    }
}
