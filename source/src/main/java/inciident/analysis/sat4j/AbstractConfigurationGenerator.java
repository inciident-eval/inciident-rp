
package inciident.analysis.sat4j;

import java.util.ArrayList;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import inciident.analysis.sat4j.solver.Sat4JSolver;
import inciident.clauses.LiteralList;
import inciident.clauses.solutions.SolutionList;
import inciident.util.data.Cache;
import inciident.util.job.InternalMonitor;


public abstract class AbstractConfigurationGenerator extends Sat4JAnalysis<SolutionList>
        implements ConfigurationGenerator {

    private int maxSampleSize = Integer.MAX_VALUE;

    protected boolean allowDuplicates = false;

    @Override
    public int getLimit() {
        return maxSampleSize;
    }

    @Override
    public void setLimit(int limit) {
        maxSampleSize = limit;
    }

    @Override
    public boolean isAllowDuplicates() {
        return allowDuplicates;
    }

    @Override
    public void setAllowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    @Override
    public int characteristics() {
        return NONNULL | IMMUTABLE;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean tryAdvance(Consumer<? super LiteralList> consumer) {
        final LiteralList literalList = get();
        if (literalList != null) {
            consumer.accept(literalList);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Spliterator<LiteralList> trySplit() {
        return null;
    }

    @Override
    public void init(Cache c, InternalMonitor monitor) {
        solver = createSolver(c.get(solverInputProvider).get());
        monitor.checkCancel();
        prepareSolver(solver);
        init(monitor);
    }

    protected void init(InternalMonitor monitor) {}

    @Override
    public final SolutionList analyze(Sat4JSolver solver, InternalMonitor monitor) throws Exception {
        init(monitor);
        monitor.setTotalWork(maxSampleSize);
        return new SolutionList(
                solver.getVariables(),
                StreamSupport.stream(this, false) //
                        .limit(maxSampleSize) //
                        .peek(c -> monitor.step()) //
                        .collect(Collectors.toCollection(ArrayList::new)));
    }
}
