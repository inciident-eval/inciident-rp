
package inciident.formula.io.textual;

import inciident.formula.structure.Formula;
import inciident.util.data.Result;
import inciident.util.io.InputMapper;
import inciident.util.io.format.Format;

public class FormulaFormat implements Format<Formula> {

    public static final String ID = FormulaFormat.class.getCanonicalName();

    @Override
    public Result<Formula> parse(InputMapper inputMapper) {
        return new NodeReader().read(inputMapper.get().readText().get());
    }

    @Override
    public String serialize(Formula object) {
        return new NodeWriter().write(object);
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String getFileExtension() {
        return "formula";
    }

    @Override
    public String getName() {
        return "Formula";
    }
}
