
package inciident.formula.io.textual;

import java.util.Arrays;

import inciident.util.data.Pair;


public class ShortSymbols extends Symbols {

    public static final Symbols INSTANCE = new ShortSymbols();

    private ShortSymbols() {
        super(
                Arrays.asList(
                        new Pair<>(Operator.NOT, "-"),
                        new Pair<>(Operator.AND, "&"),
                        new Pair<>(Operator.OR, "|"),
                        new Pair<>(Operator.IMPLIES, "=>"),
                        new Pair<>(Operator.EQUALS, "<=>")),
                false);
    }
}
