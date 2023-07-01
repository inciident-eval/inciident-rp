
package inciident.formula.io.textual;

import java.util.Arrays;

import inciident.util.data.Pair;


public class TextualSymbols extends Symbols {

    public static final Symbols INSTANCE = new TextualSymbols();

    private TextualSymbols() {
        super(
                Arrays.asList(
                        new Pair<>(Operator.NOT, "not"),
                        new Pair<>(Operator.AND, "and"),
                        new Pair<>(Operator.OR, "or"),
                        new Pair<>(Operator.IMPLIES, "implies"),
                        new Pair<>(Operator.EQUALS, "iff")),
                true);
    }
}
