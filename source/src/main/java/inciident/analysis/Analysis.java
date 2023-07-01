
package inciident.analysis;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.Assignment;
import inciident.util.data.Cache;
import inciident.util.data.Provider;
import inciident.util.job.MonitorableFunction;


public interface Analysis<T> extends MonitorableFunction<Cache, T>, Provider<T> {

    Assignment getAssumptions();

    List<Formula> getAssumedConstraints();

    void updateAssumptions();

    void resetAssumptions();
}
