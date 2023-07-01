
package inciident.formula.structure.atomic.literal;

import java.util.Collections;
import java.util.List;

import inciident.formula.structure.Terminal;
import inciident.formula.structure.term.Term;


public class False extends Terminal implements Literal {

    private static final False INSTANCE = new False();

    private False() {
        super();
    }

    public static False getInstance() {
        return INSTANCE;
    }

    @Override
    public List<? extends Term> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public True flip() {
        return Literal.True;
    }

    @Override
    public False cloneNode() {
        return this;
    }

    @Override
    public String getName() {
        return "false";
    }

    @Override
    public int hashCode() {
        return 97;
    }

    @Override
    public boolean equals(Object other) {
        return other == INSTANCE;
    }

    @Override
    public boolean equalsNode(Object other) {
        return other == INSTANCE;
    }

    @Override
    public Boolean eval(List<?> values) {
        return Boolean.FALSE;
    }
}
