
package inciident.formula.io.textual;

import java.util.Arrays;

import inciident.util.data.Pair;


public class PropositionalModelSymbols extends Symbols {

    public static final Symbols INSTANCE = new PropositionalModelSymbols();

    private PropositionalModelSymbols() {
        super(
                Arrays.asList(
                        new Pair<>(Operator.NOT, "!"),
                        new Pair<>(Operator.AND, "&"),
                        new Pair<>(Operator.OR, "|"),
                        new Pair<>(Operator.EQUALS, "=="),
                        new Pair<>(Operator.IMPLIES, "=>")),
                false);
    }
}
