
package inciident.clauses.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import inciident.clauses.LiteralList;
import inciident.util.data.Result;
import inciident.util.io.InputMapper;
import inciident.util.io.format.Format;
import inciident.util.io.format.ParseProblem;


public class ExpressionGroupFormat implements Format<List<List<List<LiteralList>>>> {

    public static final String ID = ExpressionGroupFormat.class.getSimpleName();

    @Override
    public String serialize(List<List<List<LiteralList>>> expressionGroups) {
        final StringBuilder sb = new StringBuilder();
        for (final List<? extends List<LiteralList>> expressionGroup : expressionGroups) {
            sb.append("g ");
            sb.append(expressionGroup.size());
            sb.append(System.lineSeparator());
            for (final List<LiteralList> expression : expressionGroup) {
                sb.append("e ");
                for (final LiteralList literalSet : expression) {
                    for (final int literal : literalSet.getLiterals()) {
                        sb.append(literal);
                        sb.append(" ");
                    }
                    sb.append("|");
                }
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    @Override
    public Result<List<List<List<LiteralList>>>> parse(InputMapper inputMapper) {
        final ArrayList<List<List<LiteralList>>> expressionGroups = new ArrayList<>();
        ArrayList<List<LiteralList>> expressionGroup = null;
        final Iterator<String> lineIterator = inputMapper.get().getLineStream().iterator();
        int lineCount = 0;
        try {
            while (lineIterator.hasNext()) {
                final String line = lineIterator.next();
                lineCount++;
                final char firstChar = line.charAt(0);
                switch (firstChar) {
                    case 'g':
                        final int groupSize = Integer.parseInt(line.substring(2).trim());
                        expressionGroup = new ArrayList<>(groupSize);
                        expressionGroups.add(expressionGroup);
                        break;
                    case 'e':
                        if (expressionGroup == null) {
                            throw new Exception("No group defined.");
                        }
                        final String expressionString = line.substring(2).trim();
                        final String[] clauseStrings = expressionString.split("\\|");
                        final List<LiteralList> expression = new ArrayList<>();
                        for (final String clauseString : clauseStrings) {
                            final String[] literalStrings = clauseString.split("\\s+");
                            final int[] literals = new int[literalStrings.length];
                            int index = 0;
                            for (final String literalString : literalStrings) {
                                if (!literalString.isEmpty()) {
                                    final int literal = Integer.parseInt(literalString);
                                    literals[index++] = literal;
                                }
                            }
                            expression.add(new LiteralList(Arrays.copyOfRange(literals, 0, index)));
                        }
                        expressionGroup.add(expression);
                        break;
                    default:
                        break;
                }
            }
        } catch (final Exception e) {
            return Result.empty(new ParseProblem(e, lineCount));
        }
        return Result.of(expressionGroups);
    }

    @Override
    public String getFileExtension() {
        return "expression";
    }

    @Override
    public ExpressionGroupFormat getInstance() {
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
        return "Expression Groups";
    }
}
