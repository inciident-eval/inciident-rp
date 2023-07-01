
package inciident.formula.structure.transform;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.atomic.literal.VariableMap.Constant;
import inciident.formula.structure.atomic.literal.VariableMap.Variable;
import inciident.util.tree.visitor.TreeVisitor;

public class VariableMapSetter implements TreeVisitor<Void, Formula> {

    private final VariableMap variableMap;

    public VariableMapSetter(VariableMap variableMap) {
        this.variableMap = variableMap;
    }

    private Formula replaceValueTerms(Formula node) {
        if (node instanceof Variable) {
            final Variable replacement = variableMap
                    .getVariable(node.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Map does not contain variable with name " + node.getName()));
            return replacement;
        } else if (node instanceof Constant) {
            final Constant replacement = variableMap
                    .getConstant(node.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Map does not contain constant with name " + node.getName()));
            return replacement;
        }
        return node;
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        node.mapChildren(this::replaceValueTerms);
        return TreeVisitor.super.lastVisit(path);
    }
}
