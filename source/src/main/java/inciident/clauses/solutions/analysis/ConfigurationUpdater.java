
package inciident.clauses.solutions.analysis;

import java.util.Collection;
import java.util.Optional;

import inciident.clauses.LiteralList;


public interface ConfigurationUpdater {

    Optional<LiteralList> update(LiteralList partialSolution);

    Optional<LiteralList> complete(LiteralList partialSolution, Collection<LiteralList> excludeClause);

    Optional<LiteralList> choose(Collection<LiteralList> clauses);
}
