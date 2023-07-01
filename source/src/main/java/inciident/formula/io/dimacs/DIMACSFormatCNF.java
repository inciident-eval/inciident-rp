
package inciident.formula.io.dimacs;

import java.util.Objects;

import inciident.clauses.CNF;
import inciident.clauses.LiteralList;
import inciident.util.io.format.Format;


public class DIMACSFormatCNF implements Format<CNF> {

    public static final String ID = DIMACSFormatCNF.class.getCanonicalName();

    @Override
    public String serialize(CNF cnf) {
        Objects.requireNonNull(cnf);

        final StringBuilder sb = new StringBuilder();

        // Variables
        int index = 1;
        for (final String name : cnf.getVariableMap().getVariableNames()) {
            sb.append(DIMACSConstants.COMMENT_START);
            sb.append(index++);
            sb.append(' ');
            sb.append(name);
            sb.append(System.lineSeparator());
        }

        // Problem
        sb.append(DIMACSConstants.PROBLEM);
        sb.append(' ');
        sb.append(DIMACSConstants.CNF);
        sb.append(' ');
        sb.append(cnf.getVariableMap().getVariableSignatures().size());
        sb.append(' ');
        sb.append(cnf.getClauses().size());
        sb.append(System.lineSeparator());

        // Clauses
        for (final LiteralList clause : cnf.getClauses()) {
            for (final int l : clause.getLiterals()) {
                sb.append(l);
                sb.append(' ');
            }
            sb.append(DIMACSConstants.CLAUSE_END);
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public DIMACSFormatCNF getInstance() {
        return this;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String getName() {
        return "DIMACS";
    }

    @Override
    public String getFileExtension() {
        return "dimacs";
    }
}
