
package inciident.formula.io.dimacs;

import inciident.formula.structure.Formula;
import inciident.formula.structure.Formulas;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.compound.Or;

public class DimacsWriter {

    
    private boolean writingVariableDirectory = true;

    private final Formula formula;
    private VariableMap variables;

    
    public DimacsWriter(Formula formula) throws IllegalArgumentException {
        if ((formula == null) || !Formulas.isCNF(formula)) {
            throw new IllegalArgumentException();
        }
        this.formula = Formulas.toCNF(formula).get();
    }

    
    public void setWritingVariableDirectory(boolean writingVariableDirectory) {
        this.writingVariableDirectory = writingVariableDirectory;
    }

    public boolean isWritingVariableDirectory() {
        return writingVariableDirectory;
    }

    
    public String write() {
        variables = formula.getVariableMap().orElseGet(VariableMap::new);
        final StringBuilder sb = new StringBuilder();
        if (writingVariableDirectory) {
            writeVariableDirectory(sb);
        }
        writeProblem(sb);
        writeClauses(sb);
        return sb.toString();
    }

    
    private void writeVariableDirectory(StringBuilder sb) {
        int index = 1;
        for (final String name : variables.getVariableNames()) {
            writeVariableDirectoryEntry(sb, index++, name);
        }
    }

    
    private void writeVariableDirectoryEntry(StringBuilder sb, int index, String name) {
        sb.append(DIMACSConstants.COMMENT_START);
        sb.append(index);
        sb.append(' ');
        sb.append(String.valueOf(name));
        sb.append(System.lineSeparator());
    }

    
    private void writeProblem(StringBuilder sb) {
        sb.append(DIMACSConstants.PROBLEM);
        sb.append(' ');
        sb.append(DIMACSConstants.CNF);
        sb.append(' ');
        sb.append(variables.getVariableCount());
        sb.append(' ');
        sb.append(formula.getChildren().size());
        sb.append(System.lineSeparator());
    }

    
    private void writeClause(StringBuilder sb, Or clause) {
        for (final Formula child : clause.getChildren()) {
            final Literal l = (Literal) child;
            final Integer index = variables
                    .getVariableIndex(l.getName())
                    .orElseThrow(() -> new IllegalArgumentException(l.getName()));
            sb.append(l.isPositive() ? index : -index);
            sb.append(' ');
        }
        sb.append(DIMACSConstants.CLAUSE_END);
        sb.append(System.lineSeparator());
    }

    
    private void writeClauses(StringBuilder sb) {
        for (final Formula clause : formula.getChildren()) {
            writeClause(sb, (Or) clause);
        }
    }
}
