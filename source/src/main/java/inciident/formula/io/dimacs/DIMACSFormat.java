
package inciident.formula.io.dimacs;

import java.text.ParseException;

import inciident.formula.structure.Formula;
import inciident.util.data.Result;
import inciident.util.io.InputMapper;
import inciident.util.io.format.Format;
import inciident.util.io.format.ParseProblem;


public class DIMACSFormat implements Format<Formula> {

    public static final String ID = DIMACSFormat.class.getCanonicalName();

    @Override
    public String serialize(Formula formula) {
        final DimacsWriter w = new DimacsWriter(formula);
        w.setWritingVariableDirectory(true);
        return w.write();
    }

    @Override
    public Result<Formula> parse(InputMapper inputMapper) {
        final DimacsReader r = new DimacsReader();
        r.setReadingVariableDirectory(true);
        try {
            // TODO use getLines() instead
            return Result.of(r.read(inputMapper.get().readText().get()));
        } catch (final ParseException e) {
            return Result.empty(new ParseProblem(e, e.getErrorOffset()));
        } catch (final Exception e) {
            return Result.empty(e);
        }
    }

    @Override
    public DIMACSFormat getInstance() {
        return this;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public String getName() {
        return "DIMACS";
    }

    @Override
    public String getFileExtension() {
        return "dimacs";
    }
}
