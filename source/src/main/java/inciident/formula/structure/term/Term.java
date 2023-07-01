
package inciident.formula.structure.term;

import java.util.List;

import inciident.formula.structure.Formula;

public interface Term extends Formula {

    @Override
    List<? extends Term> getChildren();

    Class<?> getType();
}
