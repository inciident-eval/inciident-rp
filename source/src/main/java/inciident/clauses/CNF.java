
package inciident.clauses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.data.Result;

// TODO rename, as it can represent both CNF and DNF

public class CNF implements Serializable {

    private static final long serialVersionUID = -7716526687669886274L;

    protected List<LiteralList> clauses;
    protected VariableMap variables;

    public CNF(VariableMap mapping, List<LiteralList> clauses) {
        variables = mapping;
        this.clauses = new ArrayList<>(clauses);
    }

    public CNF(VariableMap mapping) {
        variables = mapping;
        clauses = new ArrayList<>();
    }

    public void setClauses(List<LiteralList> clauses) {
        this.clauses = clauses;
    }

    public void addClause(LiteralList clause) {
        clauses.add(clause);
    }

    public void addClauses(Collection<LiteralList> clauses) {
        this.clauses.addAll(clauses);
    }

    public void setVariableMap(VariableMap variables) {
        this.variables = variables;
    }

    public VariableMap getVariableMap() {
        return variables;
    }

    public List<LiteralList> getClauses() {
        return clauses;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables, clauses);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final CNF other = (CNF) obj;
        return Objects.equals(variables, other.variables) && Objects.equals(clauses, other.clauses);
    }

    @Override
    public String toString() {
        return "CNF\n\tvariables=" + variables + "\n\tclauses=" + clauses;
    }

    
    public Result<CNF> adapt(VariableMap newVariableMap) {
        return LiteralList.adapt(clauses, variables, newVariableMap).map(c -> new CNF(newVariableMap, c));
    }

    public CNF randomize(Random random) {
        final VariableMap newVariableMap = variables.clone();
        newVariableMap.randomize(random);

        final List<LiteralList> adaptedClauseList =
                LiteralList.adapt(clauses, variables, newVariableMap).get();
        Collections.shuffle(adaptedClauseList, random);

        return new CNF(newVariableMap, adaptedClauseList);
    }
}
