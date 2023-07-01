
package inciident.clauses;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import inciident.formula.structure.Formula;
import inciident.formula.structure.Formulas;
import inciident.formula.structure.atomic.VariableAssignment;
import inciident.formula.structure.atomic.literal.BooleanLiteral;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.job.Executor;
import inciident.util.job.InternalMonitor;
import inciident.util.job.MonitorableFunction;
import inciident.util.tree.Trees;


public final class FormulaToCNF implements MonitorableFunction<Formula, CNF> {

    private boolean keepLiteralOrder;
    private VariableMap variableMapping;

    public static CNF convert(Formula formula) {
        return Executor.run(new FormulaToCNF(), formula).get();
    }

    public static CNF convert(Formula formula, VariableMap variableMapping) {
        final FormulaToCNF function = new FormulaToCNF();
        function.setVariableMapping(variableMapping);
        function.setKeepLiteralOrder(true);
        return Executor.run(function, formula).get();
    }

    @Override
    public CNF execute(Formula node, InternalMonitor monitor) {
        if (node == null) {
            return null;
        }
        final VariableMap mapping = variableMapping != null
                ? variableMapping
                : node.getVariableMap().orElseGet(VariableMap::new);
        final List<LiteralList> clauses = new ArrayList<>();
        final Optional<Object> formulaValue = Formulas.evaluate(node, new VariableAssignment(mapping));
        if (formulaValue.isPresent()) {
            if (formulaValue.get() == Boolean.FALSE) {
                clauses.add(new LiteralList());
            }
        } else {
            final Formula cnf = Formulas.toCNF(Trees.cloneTree(node)).get();
            cnf.getChildren().stream()
                    .map(exp -> getClause(exp, mapping))
                    .filter(Objects::nonNull)
                    .forEach(clauses::add);
        }
        return new CNF(mapping, clauses);
    }

    public boolean isKeepLiteralOrder() {
        return keepLiteralOrder;
    }

    public void setKeepLiteralOrder(boolean keepLiteralOrder) {
        this.keepLiteralOrder = keepLiteralOrder;
    }

    public VariableMap getVariableMapping() {
        return variableMapping;
    }

    public void setVariableMapping(VariableMap variableMapping) {
        this.variableMapping = variableMapping;
    }

    private LiteralList getClause(Formula clauseExpression, VariableMap mapping) {
        if (clauseExpression instanceof Literal) {
            final Literal literal = (Literal) clauseExpression;
            final int variable = mapping.getVariableSignature(literal.getName())
                    .orElseThrow(RuntimeException::new)
                    .getIndex();
            return new LiteralList(
                    new int[] {literal.isPositive() ? variable : -variable},
                    keepLiteralOrder ? LiteralList.Order.UNORDERED : LiteralList.Order.NATURAL);
        } else {
            final List<? extends Formula> clauseChildren = clauseExpression.getChildren();
            if (clauseChildren.stream().anyMatch(literal -> literal == Literal.True)) {
                return null;
            } else {
                final int[] literals = clauseChildren.stream()
                        .filter(literal -> literal != Literal.False)
                        .filter(literal -> literal instanceof BooleanLiteral)
                        .mapToInt(literal -> {
                            final int variable = mapping.getVariableSignature(((BooleanLiteral) literal)
                                            .getVariable()
                                            .getName())
                                    .orElseThrow(RuntimeException::new)
                                    .getIndex();
                            return ((Literal) literal).isPositive() ? variable : -variable;
                        })
                        .toArray();
                return new LiteralList(
                        literals, keepLiteralOrder ? LiteralList.Order.UNORDERED : LiteralList.Order.NATURAL);
            }
        }
    }
}
