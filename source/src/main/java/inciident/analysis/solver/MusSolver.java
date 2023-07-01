
package inciident.analysis.solver;

import java.util.List;


public interface MusSolver<T> extends SatSolver {

    
    List<T> getMinimalUnsatisfiableSubset() throws IllegalStateException;

    
    List<List<T>> getAllMinimalUnsatisfiableSubsets() throws IllegalStateException;
}
