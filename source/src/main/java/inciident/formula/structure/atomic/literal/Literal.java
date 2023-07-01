
package inciident.formula.structure.atomic.literal;

import inciident.formula.structure.atomic.Atomic;


public interface Literal extends Atomic {

    True True = inciident.formula.structure.atomic.literal.True.getInstance();

    False False = inciident.formula.structure.atomic.literal.False.getInstance();

    @Override
    Literal flip();

    @Override
    Literal cloneNode();

    default boolean isPositive() {
        return true;
    }
}
