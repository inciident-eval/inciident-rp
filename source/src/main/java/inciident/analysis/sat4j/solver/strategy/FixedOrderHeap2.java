
package inciident.analysis.sat4j.solver.strategy;

public class FixedOrderHeap2 extends FixedOrderHeap {

    private static final long serialVersionUID = 1L;

    private final UniformRandomSelectionStrategy selectionStrategy;

    public FixedOrderHeap2(UniformRandomSelectionStrategy strategy, int[] order) {
        super(strategy, order);
        selectionStrategy = strategy;
    }

    @Override
    public void undo(int x) {
        super.undo(x);
        selectionStrategy.undo(x);
    }
}
