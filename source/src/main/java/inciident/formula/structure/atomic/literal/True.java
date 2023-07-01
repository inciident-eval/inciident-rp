
package inciident.formula.structure.atomic.literal;

import java.util.Collections;
import java.util.List;

import inciident.formula.structure.Terminal;
import inciident.formula.structure.term.Term;


public final class True extends Terminal implements Literal {

    private static final True INSTANCE = new True();

    private True() {
        super();
    }

    public static True getInstance() {
        return INSTANCE;
    }

    @Override
    public List<? extends Term> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public False flip() {
        return Literal.False;
    }

    @Override
    public True cloneNode() {
        return this;
    }

    @Override
    public String getName() {
        return "true";
    }

    @Override
    public int hashCode() {
        return 91;
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
        return Boolean.TRUE;
    }
}
