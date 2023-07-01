
package inciident.formula.structure;

import java.nio.file.Path;

import inciident.formula.io.FormulaFormatManager;
import inciident.formula.structure.transform.CNFTransformer;
import inciident.formula.structure.transform.DNFTransformer;
import inciident.util.data.Cache;
import inciident.util.data.Identifier;
import inciident.util.data.Provider;
import inciident.util.data.Result;
import inciident.util.job.InternalMonitor;


@FunctionalInterface
public interface FormulaProvider extends Provider<Formula> {

    Identifier<Formula> identifier = new Identifier<>();

    @Override
    default Identifier<Formula> getIdentifier() {
        return identifier;
    }

    static FormulaProvider empty() {
        return (c, m) -> Result.empty();
    }

    static FormulaProvider of(Formula formula) {
        return (c, m) -> Result.of(formula);
    }

    static FormulaProvider in(Cache cache) {
        return (c, m) -> cache.get(identifier);
    }

    static FormulaProvider loader(Path path) {
        return (c, m) -> Provider.load(path, FormulaFormatManager.getInstance());
    }

    public static class CNF implements FormulaProvider {
        public static final Identifier<Formula> identifier = new Identifier<>();
        private final int maximumNumberOfLiterals;

        private CNF() {
            this(Integer.MAX_VALUE);
        }

        private CNF(int maximumNumberOfLiterals) {
            this.maximumNumberOfLiterals = maximumNumberOfLiterals;
        }

        @Override
        public Object getParameters() {
            return maximumNumberOfLiterals;
        }

        @Override
        public Identifier<Formula> getIdentifier() {
            return identifier;
        }

        @Override
        public Result<Formula> apply(Cache c, InternalMonitor m) {
            final CNFTransformer cnfTransformer = new CNFTransformer();
            cnfTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
            return Provider.convert(c, FormulaProvider.identifier, cnfTransformer, m);
        }

        public static CNF fromFormula() {
            return new CNF();
        }

        public static CNF fromFormula(int maximumNumberOfLiterals) {
            return new CNF(maximumNumberOfLiterals);
        }
    }

    @FunctionalInterface
    interface DNF extends FormulaProvider {
        Identifier<Formula> identifier = new Identifier<>();

        @Override
        default Identifier<Formula> getIdentifier() {
            return identifier;
        }

        static DNF fromFormula() {
            return (c, m) -> Provider.convert(c, FormulaProvider.identifier, new DNFTransformer(), m);
        }
    }
}
