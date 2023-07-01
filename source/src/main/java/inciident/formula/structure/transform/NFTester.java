
package inciident.formula.structure.transform;

import java.util.Optional;

import inciident.formula.structure.Formula;
import inciident.util.tree.visitor.TreeVisitor;

public class NFTester implements TreeVisitor<Boolean, Formula> {

    protected boolean isNf;
    protected boolean isClausalNf;

    @Override
    public void reset() {
        isNf = true;
        isClausalNf = true;
    }

    @Override
    public Optional<Boolean> getResult() {
        return Optional.of(isNf);
    }

    public boolean isNf() {
        return isNf;
    }

    public boolean isClausalNf() {
        return isClausalNf;
    }
}
