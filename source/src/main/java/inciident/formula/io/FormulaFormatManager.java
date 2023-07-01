
package inciident.formula.io;

import inciident.formula.structure.Formula;
import inciident.util.io.format.FormatManager;

public final class FormulaFormatManager extends FormatManager<Formula> {

    private static FormulaFormatManager INSTANCE = new FormulaFormatManager();

    public static FormulaFormatManager getInstance() {
        return INSTANCE;
    }

    private FormulaFormatManager() {}
}
