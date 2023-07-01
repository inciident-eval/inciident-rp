
package inciident.analysis.sat4j;

import inciident.clauses.LiteralList;


public abstract class AVariableAnalysis<T> extends Sat4JAnalysis<T> {

    protected LiteralList variables;

    public LiteralList getVariables() {
        return variables;
    }

    public void setVariables(LiteralList variables) {
        this.variables = variables;
    }
}
