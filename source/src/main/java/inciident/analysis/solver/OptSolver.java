
package inciident.analysis.solver;


public interface OptSolver<T, V> extends Solver {

    T minimum(V formula);

    T maximum(V formula);
}
