
package inciident.formula.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class AuxiliaryRoot extends NonTerminal {

    public AuxiliaryRoot(Formula node) {
        super(node);
    }

    private AuxiliaryRoot() {
        super();
    }

    @Override
    public String getName() {
        return "";
    }

    public Formula getChild() {
        return getChildren().iterator().next();
    }

    public void setChild(Formula node) {
        Objects.requireNonNull(node);
        setChildren(Arrays.asList(node));
    }

    @Override
    public AuxiliaryRoot cloneNode() {
        return new AuxiliaryRoot();
    }

    @Override
    public Class<?> getType() {
        return getChild().getType();
    }

    @Override
    public Object eval(List<?> values) {
        return getChild().eval(values);
    }
}
