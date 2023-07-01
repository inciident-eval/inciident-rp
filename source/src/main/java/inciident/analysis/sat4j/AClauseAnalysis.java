
package inciident.analysis.sat4j;

import java.util.List;

import inciident.clauses.LiteralList;


public abstract class AClauseAnalysis<T> extends Sat4JAnalysis<T> {

    protected List<LiteralList> clauseList;
    protected int[] clauseGroupSize;

    public List<LiteralList> getClauseList() {
        return clauseList;
    }

    public void setClauseList(List<LiteralList> clauseList) {
        this.clauseList = clauseList;
    }

    public int[] getClauseGroups() {
        return clauseGroupSize;
    }

    public void setClauseGroupSize(int[] clauseGroups) {
        this.clauseGroupSize = clauseGroups;
    }
}
