
package inciident.formula.io.dimacs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.BooleanLiteral;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.atomic.literal.VariableMap;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Or;
import inciident.util.io.NonEmptyLineIterator;

public class DimacsReader {

    private static final Pattern commentPattern = Pattern.compile("\\A" + DIMACSConstants.COMMENT + "\\s*(.*)\\Z");
    private static final Pattern problemPattern = Pattern.compile(
            "\\A\\s*" + DIMACSConstants.PROBLEM + "\\s+" + DIMACSConstants.CNF + "\\s+(\\d+)\\s+(\\d+)");

    
    private final Map<Integer, String> indexVariables = new LinkedHashMap<>();

    private VariableMap map;

    
    private int variableCount;
    
    private int clauseCount;
    
    private boolean readVariableDirectory = false;
    
    private boolean readingVariables;

    
    public void setReadingVariableDirectory(boolean readVariableDirectory) {
        this.readVariableDirectory = readVariableDirectory;
    }

    
    public Formula read(Reader in) throws ParseException, IOException {
        indexVariables.clear();
        variableCount = -1;
        clauseCount = -1;
        readingVariables = readVariableDirectory;
        if (!readVariableDirectory) {
            map = new VariableMap();
        }
        try (final BufferedReader reader = new BufferedReader(in)) {
            final NonEmptyLineIterator nonemptyLineIterator = new NonEmptyLineIterator(reader);
            nonemptyLineIterator.get();

            readComments(nonemptyLineIterator);
            readProblem(nonemptyLineIterator);
            readComments(nonemptyLineIterator);
            readingVariables = false;

            if (readVariableDirectory) {
                for (int i = 1; i <= variableCount; i++) {
                    indexVariables.putIfAbsent(i, Integer.toString(i));
                }
                map = new VariableMap();
                indexVariables.forEach((i, n) -> map.addBooleanVariable(n, i));
            }

            final List<Or> clauses = readClauses(nonemptyLineIterator);
            final int actualVariableCount = indexVariables.size();
            final int actualClauseCount = clauses.size();
            if (variableCount != actualVariableCount) {
                throw new ParseException(
                        String.format("Found %d instead of %d variables", actualVariableCount, variableCount), 1);
            }
            if (clauseCount != actualClauseCount) {
                throw new ParseException(
                        String.format("Found %d instead of %d clauses", actualClauseCount, clauseCount), 1);
            }
            return new And(clauses);
        }
    }

    private void readComments(final NonEmptyLineIterator nonemptyLineIterator) {
        for (String line = nonemptyLineIterator.currentLine(); line != null; line = nonemptyLineIterator.get()) {
            final Matcher matcher = commentPattern.matcher(line);
            if (matcher.matches()) {
                readComment(matcher.group(1)); // read comments ...
            } else {
                break; // ... until a non-comment token is found.
            }
        }
    }

    
    public Formula read(String in) throws ParseException, IOException {
        return read(new StringReader(in));
    }

    
    private void readProblem(NonEmptyLineIterator nonemptyLineIterator) throws ParseException {
        final String line = nonemptyLineIterator.currentLine();
        if (line == null) {
            throw new ParseException("Invalid problem format", nonemptyLineIterator.getLineCount());
        }
        final Matcher matcher = problemPattern.matcher(line);
        if (!matcher.find()) {
            throw new ParseException("Invalid problem format", nonemptyLineIterator.getLineCount());
        }
        final String trail = line.substring(matcher.end());
        if (trail.trim().isEmpty()) {
            nonemptyLineIterator.get();
        } else {
            nonemptyLineIterator.setCurrentLine(trail);
        }

        try {
            variableCount = Integer.parseInt(matcher.group(1));
        } catch (final NumberFormatException e) {
            throw new ParseException("Variable count is not an integer", nonemptyLineIterator.getLineCount());
        }
        if (variableCount < 0) {
            throw new ParseException("Variable count is not positive", nonemptyLineIterator.getLineCount());
        }

        try {
            clauseCount = Integer.parseInt(matcher.group(2));
        } catch (final NumberFormatException e) {
            throw new ParseException("Clause count is not an integer", nonemptyLineIterator.getLineCount());
        }
        if (clauseCount < 0) {
            throw new ParseException("Clause count is not positive", nonemptyLineIterator.getLineCount());
        }
    }

    
    private List<Or> readClauses(NonEmptyLineIterator nonemptyLineIterator) throws ParseException, IOException {
        final LinkedList<String> literalQueue = new LinkedList<>();
        final List<Or> clauses = new ArrayList<>(clauseCount);
        int readClausesCount = 0;
        for (String line = nonemptyLineIterator.currentLine(); line != null; line = nonemptyLineIterator.get()) {
            if (commentPattern.matcher(line).matches()) {
                continue;
            }
            List<String> literalList = Arrays.asList(line.trim().split("\\s+"));
            literalQueue.addAll(literalList);

            do {
                final int clauseEndIndex = literalList.indexOf("0");
                if (clauseEndIndex < 0) {
                    break;
                }
                final int clauseSize = literalQueue.size() - (literalList.size() - clauseEndIndex);
                if (clauseSize < 0) {
                    throw new ParseException("Invalid clause", nonemptyLineIterator.getLineCount());
                } else if (clauseSize == 0) {
                    clauses.add(new Or());
                } else {
                    clauses.add(parseClause(readClausesCount, clauseSize, literalQueue, nonemptyLineIterator));
                }
                readClausesCount++;

                if (!DIMACSConstants.CLAUSE_END.equals(literalQueue.removeFirst())) {
                    throw new ParseException("Illegal clause end", nonemptyLineIterator.getLineCount());
                }
                literalList = literalQueue;
            } while (!literalQueue.isEmpty());
        }
        if (!literalQueue.isEmpty()) {
            clauses.add(parseClause(readClausesCount, literalQueue.size(), literalQueue, nonemptyLineIterator));
            readClausesCount++;
        }
        if (readClausesCount < clauseCount) {
            throw new ParseException(String.format("Found %d instead of %d clauses", readClausesCount, clauseCount), 1);
        }
        return clauses;
    }

    private Or parseClause(
            int readClausesCount,
            int clauseSize,
            LinkedList<String> literalQueue,
            NonEmptyLineIterator nonemptyLineIterator)
            throws ParseException {
        if (readClausesCount == clauseCount) {
            throw new ParseException(String.format("Found more than %d clauses", clauseCount), 1);
        }
        final Literal[] literals = new Literal[clauseSize];
        for (int j = 0; j < literals.length; j++) {
            final String token = literalQueue.removeFirst();
            final int index;
            try {
                index = Integer.parseInt(token);
            } catch (final NumberFormatException e) {
                throw new ParseException("Illegal literal", nonemptyLineIterator.getLineCount());
            }
            if (index == 0) {
                throw new ParseException("Illegal literal", nonemptyLineIterator.getLineCount());
            }
            final Integer key = Math.abs(index);
            String variableName = indexVariables.get(key);
            if (variableName == null) {
                variableName = String.valueOf(key);
                indexVariables.put(key, variableName);
            }
            if (map.getVariableIndex(variableName).isEmpty()) {
                map.addBooleanVariable(variableName);
            }
            literals[j] =
                    new BooleanLiteral(map.getVariableSignature(variableName).get(), index > 0);
        }
        return new Or(literals);
    }

    
    private boolean readComment(String comment) {
        return readingVariables && readVariableDirectoryEntry(comment);
    }

    
    private boolean readVariableDirectoryEntry(String comment) {
        final int firstSeparator = comment.indexOf(' ');
        if (firstSeparator <= 0) {
            return false;
        }
        final int index;
        try {
            index = Integer.parseInt(comment.substring(0, firstSeparator));
        } catch (final NumberFormatException e) {
            return false;
        }
        if (comment.length() < (firstSeparator + 2)) {
            return false;
        }
        final String variable = comment.substring(firstSeparator + 1);
        if (!indexVariables.containsKey(index)) {
            indexVariables.put(index, variable);
        }
        return true;
    }
}
