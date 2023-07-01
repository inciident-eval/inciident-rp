
package inciident.clauses.solutions.io;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import inciident.clauses.LiteralList;
import inciident.clauses.solutions.SolutionList;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.util.data.Problem.Severity;
import inciident.util.data.Result;
import inciident.util.io.InputMapper;
import inciident.util.io.format.Format;
import inciident.util.io.format.ParseProblem;


public class ListFormat implements Format<SolutionList> {

    public static final String ID = ListFormat.class.getCanonicalName();

    @Override
    public String serialize(SolutionList configurationList) {
        final StringBuilder csv = new StringBuilder();
        csv.append("Configuration");
        final List<String> names = configurationList.getVariableMap().getVariableNames();
        for (final String name : names) {
            csv.append(';');
            csv.append(name);
        }
        csv.append('\n');
        int configurationIndex = 0;
        for (final LiteralList configuration : configurationList.getSolutions()) {
            csv.append(configurationIndex++);
            final int[] literals = configuration.getLiterals();
            for (int i = 0; i < literals.length; i++) {
                csv.append(';');
                csv.append(literals[i] < 0 ? 0 : 1);
            }
            csv.append('\n');
        }
        return csv.toString();
    }

    @Override
    public Result<SolutionList> parse(InputMapper inputMapper) {
        int lineNumber = 0;
        final SolutionList configurationList = new SolutionList();
        final Iterator<String> iterator = inputMapper.get().getLineStream().iterator();
        try {
            {
                if (!iterator.hasNext()) {
                    return Result.empty(new ParseProblem("Empty file!", lineNumber, Severity.ERROR));
                }
                final String line = iterator.next();
                if (line.trim().isEmpty()) {
                    return Result.empty(new ParseProblem("Empty file!", lineNumber, Severity.ERROR));
                }
                final String[] names = line.split(";");
                final VariableMap map = new VariableMap();
                Arrays.asList(names).subList(1, names.length).forEach(map::addBooleanVariable);
                configurationList.setVariables(map);
            }

            while (iterator.hasNext()) {
                final String line = iterator.next();
                lineNumber++;
                final String[] split = line.split(";");
                if ((split.length - 1)
                        != configurationList
                                .getVariableMap()
                                .getVariableSignatures()
                                .size()) {
                    return Result.empty(new ParseProblem(
                            "Number of selections does not match number of features!", lineNumber, Severity.ERROR));
                }
                final int[] literals = new int
                        [configurationList
                                .getVariableMap()
                                .getVariableSignatures()
                                .size()];
                for (int i = 1; i < split.length; i++) {
                    literals[i - 1] = split[i].equals("0") ? -i : i;
                }
                configurationList.addSolution(new LiteralList(literals, LiteralList.Order.INDEX, false));
            }
        } catch (final Exception e) {
            return Result.empty(new ParseProblem(e.getMessage(), lineNumber, Severity.ERROR));
        }
        return Result.of(configurationList);
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }

    @Override
    public ListFormat getInstance() {
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
        return "ConfigurationList";
    }
}
