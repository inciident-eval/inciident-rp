
package inciident.formula.structure.term;

import java.util.List;

import inciident.formula.structure.NonTerminal;

public abstract class Function extends NonTerminal implements Term {

    public Function(List<Term> nodes) {
        super(nodes);
    }

    public Function(Term... nodes) {
        super(nodes);
    }

    public Function() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Term> getChildren() {
        return (List<Term>) super.getChildren();
    }
}
