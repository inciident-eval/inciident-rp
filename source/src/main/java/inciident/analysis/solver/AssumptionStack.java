
package inciident.analysis.solver;

import java.util.ArrayDeque;
import java.util.Deque;


public class AssumptionStack<T> implements Assumptions<T> {

    protected final ArrayDeque<T> assumptions;

    public Deque<T> getAssumptions() {
        return assumptions;
    }

    public AssumptionStack() {
        assumptions = new ArrayDeque<>();
    }

    public AssumptionStack(int size) {
        assumptions = new ArrayDeque<>(size);
    }

    protected AssumptionStack(AssumptionStack<T> oldAssumptions) {
        assumptions = new ArrayDeque<>(oldAssumptions.assumptions);
    }

    @Override
    public void clear() {
        assumptions.clear();
    }

    @Override
    public T pop() {
        return assumptions.pop();
    }

    @Override
    public void push(T var) {
        assumptions.push(var);
    }

    @Override
    public int size() {
        return assumptions.size();
    }

    @Override
    public T peek() {
        return assumptions.peek();
    }
}
