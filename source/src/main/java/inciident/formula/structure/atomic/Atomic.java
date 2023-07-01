
package inciident.formula.structure.atomic;

import java.util.List;

import inciident.formula.structure.Formula;
import inciident.formula.structure.term.Term;


public interface Atomic extends Formula {

    @Override
    List<? extends Term> getChildren();

    Atomic flip();

    default Class<?> getType() {
        return Boolean.class;
    }
}
