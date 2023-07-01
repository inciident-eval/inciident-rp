
package inciident.formula.io.textual;

import java.util.List;

import inciident.formula.io.textual.Symbols.Operator;
import inciident.formula.structure.Formula;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.formula.structure.compound.And;
import inciident.formula.structure.compound.Biimplies;
import inciident.formula.structure.compound.Compound;
import inciident.formula.structure.compound.Implies;
import inciident.formula.structure.compound.Not;
import inciident.formula.structure.compound.Or;


public class NodeWriter {

    
    public enum Notation {
        
        INFIX,
        
        PREFIX,
        
        POSTFIX,
    }

    
    public enum LineFormat {
        
        SINGLE,
        
        TREE,
    }

    
    private Symbols symbols = ShortSymbols.INSTANCE;
    
    private Notation notation = Notation.INFIX;
    
    private LineFormat lineFormat = LineFormat.SINGLE;
    
    private boolean enforceBrackets = false;
    
    private boolean enquoteWhitespace = false;

    private String separator = ",";
    private String tab = "\t";
    private String newLine = System.lineSeparator();

    
    public void setSymbols(Symbols symbols) {
        this.symbols = symbols;
    }

    
    protected Symbols getSymbols() {
        return symbols;
    }

    
    public void setNotation(Notation notation) {
        this.notation = notation;
    }

    
    protected Notation getNotation() {
        return notation;
    }

    public LineFormat getLineFormat() {
        return lineFormat;
    }

    public void setLineFormat(LineFormat lineFormat) {
        this.lineFormat = lineFormat;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getSeparator() {
        return separator;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getNewLine() {
        return newLine;
    }

    public void setNewLine(String newLine) {
        this.newLine = newLine;
    }

    
    public void setEnforceBrackets(boolean enforceBrackets) {
        this.enforceBrackets = enforceBrackets;
    }

    
    protected boolean isEnforceBrackets() {
        return enforceBrackets;
    }

    
    public void setEnquoteWhitespace(boolean enquoteWhitespace) {
        this.enquoteWhitespace = enquoteWhitespace;
    }

    
    protected boolean isEnquoteWhitespace() {
        return enquoteWhitespace;
    }

    
    public String write(Formula formula) {
        final StringBuilder sb = new StringBuilder();
        nodeToString(formula, null, sb, -1);
        return sb.toString();
    }

    public void write(Formula formula, StringBuilder sb) {
        nodeToString(formula, null, sb, -1);
    }

    private void nodeToString(Formula formula, Operator parent, StringBuilder sb, int depth) {
        if (formula == null) {
            sb.append(String.valueOf(formula));
        } else {
            if (formula instanceof Not) {
                final Formula child = ((Not) formula).getChildren().get(0);
                if (child instanceof Literal) {
                    literalToString(((Literal) child).cloneNode().flip(), sb, depth + 1);
                    return;
                }
            }
            if (formula instanceof Literal) {
                literalToString((Literal) formula, sb, depth + 1);
            } else {
                operationToString((Compound) formula, parent, sb, depth + 1);
            }
        }
    }

    
    private void literalToString(Literal l, StringBuilder sb, int depth) {
        alignLine(sb, depth);
        final String s = variableToString(l.getName());
        if (!l.isPositive()) {
            final Notation notation = getNotation();
            switch (notation) {
                case INFIX:
                    sb.append(getSymbols().getSymbol(Operator.NOT));
                    sb.append(getSymbols().isTextual() ? " " : "");
                    sb.append(s);
                    break;
                case PREFIX:
                    sb.append('(');
                    sb.append(getSymbols().getSymbol(Operator.NOT));
                    sb.append(' ');
                    sb.append(s);
                    sb.append(')');
                    break;
                case POSTFIX:
                    sb.append('(');
                    sb.append(s);
                    sb.append(' ');
                    sb.append(getSymbols().getSymbol(Operator.NOT));
                    sb.append(')');
                    break;
                default:
                    throw new IllegalStateException("Unknown notation: " + notation);
            }
        } else {
            sb.append(s);
        }
    }

    
    private String variableToString(String variable) {
        return (isEnquoteWhitespace() && (containsWhitespace(variable) || equalsSymbol(variable)))
                ? '"' + variable + '"'
                : variable;
    }

    
    private void operationToString(Compound node, Operator parent, StringBuilder sb, int depth) {
        alignLine(sb, depth);
        final List<Formula> children = node.getChildren();
        if (children.size() == 0) {
            sb.append("()");
            return;
        }

        final Operator operator = Symbols.getOperator(node);
        final Notation notation = getNotation();
        switch (notation) {
            case INFIX:
                if (isInfixCompatibleOperation(node)) {
                    final int orderParent;
                    final int orderChild;
                    final boolean parenthesis = (isEnforceBrackets()
                            || ((orderParent = getSymbols().getOrder(parent))
                                    > (orderChild = getSymbols().getOrder(operator)))
                            || ((orderParent == orderChild)
                                    && (orderParent == getSymbols().getOrder(Operator.IMPLIES))));
                    if (parenthesis) {
                        sb.append('(');
                    }
                    nodeToString(children.get(0), operator, sb, depth);
                    for (int i = 1; i < children.size(); i++) {
                        sb.append(' ');
                        sb.append(getSymbols().getSymbol(operator));
                        sb.append(' ');
                        nodeToString(children.get(i), operator, sb, depth);
                    }
                    if (parenthesis) {
                        sb.append(')');
                    }
                } else {
                    sb.append(getSymbols().getSymbol(operator));
                    if ((node instanceof Not) && (getSymbols().isTextual())) {
                        sb.append(' ');
                    }
                    sb.append('(');
                    nodeToString(children.get(0), operator, sb, depth);
                    for (int i = 1; i < children.size(); i++) {
                        sb.append(getSeparator());
                        nodeToString(children.get(i), operator, sb, depth);
                    }
                    sb.append(')');
                }
                break;
            case PREFIX:
                sb.append('(');
                sb.append(getSymbols().getSymbol(operator));
                sb.append(' ');
                nodeToString(children.get(0), operator, sb, depth);
                for (int i = 1; i < children.size(); i++) {
                    sb.append(' ');
                    nodeToString(children.get(i), operator, sb, depth);
                }
                sb.append(')');

                break;
            case POSTFIX:
                sb.append('(');
                nodeToString(children.get(0), operator, sb, depth);
                for (int i = 1; i < children.size(); i++) {
                    sb.append(' ');
                    nodeToString(children.get(i), operator, sb, depth);
                }
                sb.append(' ');
                sb.append(getSymbols().getSymbol(operator));
                sb.append(')');
                break;
            default:
                throw new IllegalStateException("Unknown notation: " + notation);
        }
    }

    private void alignLine(StringBuilder sb, int depth) {
        switch (lineFormat) {
            case SINGLE:
                break;
            case TREE:
                if (depth > 0) {
                    sb.append('\n');
                    for (int i = 0; i < depth; i++) {
                        sb.append('\t');
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unknown line format: " + lineFormat);
        }
    }

    
    private boolean isInfixCompatibleOperation(Formula node) {
        return (node instanceof And)
                || (node instanceof Or)
                || (node instanceof Implies)
                || (node instanceof Biimplies);
    }

    
    private boolean equalsSymbol(String s) {
        return getSymbols().parseSymbol(s) != Operator.UNKNOWN;
    }

    
    private static boolean containsWhitespace(String s) {
        return s.matches(".*?\\s+.*");
    }
}
