
package inciident.clauses;

import java.nio.file.Path;

import inciident.formula.io.FormulaFormatManager;
import inciident.formula.structure.FormulaProvider;
import inciident.util.data.Cache;
import inciident.util.data.Identifier;
import inciident.util.data.Provider;
import inciident.util.data.Result;


@FunctionalInterface
public interface CNFProvider extends Provider<CNF> {

    Identifier<CNF> identifier = new Identifier<>();

    @Override
    default Identifier<CNF> getIdentifier() {
        return identifier;
    }

    static CNFProvider empty() {
        return (c, m) -> Result.empty();
    }

    static CNFProvider of(CNF cnf) {
        return (c, m) -> Result.of(cnf);
    }

    static CNFProvider in(Cache cache) {
        return (c, m) -> cache.get(identifier);
    }

    static CNFProvider loader(Path path) {
        return (c, m) -> Provider.load(path, FormulaFormatManager.getInstance()).map(Clauses::convertToCNF);
    }

    static <T> CNFProvider fromFormula() {
        return (c, m) -> Provider.convert(c, FormulaProvider.CNF.fromFormula(), new FormulaToCNF(), m);
    }

    static <T> CNFProvider fromTseytinFormula() {
        return (c, m) -> Provider.convert(c, FormulaProvider.CNF.fromFormula(0), new FormulaToCNF(), m);
    }
}
