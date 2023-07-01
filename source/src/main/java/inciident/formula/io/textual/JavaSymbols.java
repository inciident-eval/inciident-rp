
package inciident.formula.io.textual;

import java.util.Arrays;

import inciident.util.data.Pair;


public class JavaSymbols extends Symbols {

    public static final Symbols INSTANCE = new JavaSymbols();

    private JavaSymbols() {
        super(
                Arrays.asList(
                        new Pair<>(Operator.NOT, "!"),
                        new Pair<>(Operator.AND, "&&"),
                        new Pair<>(Operator.OR, "||"),
                        new Pair<>(Operator.EQUALS, "==")),
                false);
    }
}
