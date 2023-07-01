
package inciident.analysis.solver;


public interface SolutionSolver<T> extends SatSolver {

    
    T getSolution();

    
    default T findSolution() {
        return hasSolution() == SatResult.TRUE ? getSolution() : null;
    }
}
