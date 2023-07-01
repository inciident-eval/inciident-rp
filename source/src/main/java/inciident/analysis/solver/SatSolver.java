
package inciident.analysis.solver;


public interface SatSolver extends Solver {

    
    enum SatResult {
        FALSE,
        TIMEOUT,
        TRUE
    }

    
    SatResult hasSolution();
}
