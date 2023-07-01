
package inciident.formula.io.textual;

import java.util.Arrays;

import inciident.util.data.Pair;


public class LogicalSymbols extends Symbols {

    public static final Symbols INSTANCE = new LogicalSymbols();

    private LogicalSymbols() {
        super(
                Arrays.asList(
                        new Pair<>(Operator.NOT, "\u00AC"),
                        new Pair<>(Operator.AND, "\u2227"),
                        new Pair<>(Operator.OR, "\u2228"),
                        new Pair<>(Operator.IMPLIES, "\u21D2"),
                        new Pair<>(Operator.EQUALS, "\u21D4")),
                false);
    }
}
