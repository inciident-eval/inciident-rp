
package inciident.clauses.solutions.analysis;

import inciident.clauses.LiteralList;

@FunctionalInterface
public interface ConfigurationVerifyer {

    int test(LiteralList configuration);
}
