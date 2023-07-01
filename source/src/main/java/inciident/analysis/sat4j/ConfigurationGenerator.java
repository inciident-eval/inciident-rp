
package inciident.analysis.sat4j;

import java.util.Spliterator;
import java.util.function.Supplier;

import inciident.analysis.Analysis;
import inciident.clauses.LiteralList;
import inciident.clauses.solutions.SolutionList;
import inciident.util.data.Cache;
import inciident.util.job.InternalMonitor;


public interface ConfigurationGenerator
        extends Analysis<SolutionList>, Supplier<LiteralList>, Spliterator<LiteralList> {

    void init(Cache rep, InternalMonitor monitor);

    int getLimit();

    void setLimit(int limit);

    boolean isAllowDuplicates();

    void setAllowDuplicates(boolean allowDuplicates);
}
