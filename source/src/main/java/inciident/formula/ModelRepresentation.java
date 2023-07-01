
package inciident.formula;

import java.nio.file.Path;

import inciident.formula.io.FormulaFormatManager;
import inciident.formula.structure.Formula;
import inciident.formula.structure.FormulaProvider;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.data.Cache;
import inciident.util.data.Provider;
import inciident.util.data.Result;
import inciident.util.io.IO;
import inciident.util.job.InternalMonitor;
import inciident.util.logging.Logger;


public class ModelRepresentation {

    private final Cache cache = new Cache();
    private final Formula formula;
    private final VariableMap variables;

    public static Result<ModelRepresentation> load(final Path modelFile) {
        return IO.load(modelFile, FormulaFormatManager.getInstance()) //
                .map(ModelRepresentation::new);
    }

    public ModelRepresentation(Formula formula) {
        this.formula = formula;
        this.variables = formula.getVariableMap().orElseThrow();
        cache.set(FormulaProvider.of(formula));
    }

    public <T> Result<T> getResult(Provider<T> provider) {
        return cache.get(provider, null);
    }

    // todo: also allow to use extensions
    public <T> T get(Provider<T> provider) {
        return cache.get(provider).orElse(Logger::logProblems);
    }

    public Cache getCache() {
        return cache;
    }

    public Formula getFormula() {
        return formula;
    }

    public VariableMap getVariables() {
        return variables;
    }

    public <T> Result<T> getResult(Provider<T> provider, InternalMonitor monitor) {
        return cache.get(provider, monitor);
    }
}
