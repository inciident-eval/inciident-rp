
package inciident.formula.io;

import java.util.ArrayList;
import java.util.stream.Collectors;

import inciident.formula.io.textual.NodeReader;
import inciident.formula.io.textual.PropositionalModelSymbols;
import inciident.formula.structure.Formula;
import inciident.formula.structure.compound.And;
import inciident.util.data.Problem;
import inciident.util.data.Result;
import inciident.util.io.InputMapper;
import inciident.util.io.format.Format;

public class KConfigReaderFormat implements Format<Formula> {

    public static final String ID = KConfigReaderFormat.class.getCanonicalName();

    @Override
    public Result<Formula> parse(InputMapper inputMapper) {
        final ArrayList<Problem> problems = new ArrayList<>();
        final NodeReader nodeReader = new NodeReader();
        nodeReader.setSymbols(PropositionalModelSymbols.INSTANCE);
        return Result.of(
                new And(inputMapper
                        .get()
                        .getLineStream() //
                        .map(String::trim) //
                        .filter(l -> !l.isEmpty()) //
                        .filter(l -> !l.startsWith("#")) //
                        // fix non-boolean constraints
                        .map(l -> l.replace("=", "_"))
                        .map(l -> l.replace(":", "_"))
                        .map(l -> l.replace(".", "_"))
                        .map(l -> l.replace(",", "_"))
                        .map(l -> l.replace("/", "_"))
                        .map(l -> l.replace("\\", "_"))
                        .map(l -> l.replace(" ", "_"))
                        .map(l -> l.replace("-", "_"))
                        .map(l -> l.replaceAll("def\\((\\w+)\\)", "$1"))
                        .map(nodeReader::read) //
                        .peek(r -> problems.addAll(r.getProblems()))
                        .filter(Result::isPresent)
                        .map(Result::get) //
                        .collect(Collectors.toList())),
                problems);
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public boolean supportsSerialize() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String getFileExtension() {
        return "model";
    }

    @Override
    public String getName() {
        return "KConfigReader";
    }
}
