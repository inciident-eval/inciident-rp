
package inciident.formula.io.textual;

import java.util.List;

import inciident.formula.io.textual.Symbols.Operator;
import inciident.formula.structure.Formula;
import inciident.formula.structure.Terminal;
import inciident.formula.structure.atomic.literal.Literal;
import inciident.util.tree.visitor.DfsVisitor;
import inciident.util.tree.visitor.TreeVisitor;


public class NodeWriter2 implements DfsVisitor<Void, Formula> {

    private final StringBuilder sb = new StringBuilder();

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Notation notation = getNotation();
        switch (notation) {
            case INFIX:
                // if literal or not infixable or != 2 children
                final Formula currentNode = TreeVisitor.getCurrentNode(path);

                alignLine(path.size());
                if (currentNode instanceof Terminal) {
                    if (currentNode instanceof Literal) {
                        if (!((Literal) currentNode).isPositive()) {
                            sb.append(getSymbols().getSymbol(Operator.NOT));
                            sb.append(getSymbols().isTextual() ? " " : "");
                        }
                    }
                    sb.append(variableToString(currentNode.getName()));
                } else if (currentNode.getChildren().size() != 2) {

                } else if (currentNode instanceof Formula) {
                    Symbols.getOperator((Formula) currentNode);
                }
                break;
            case PREFIX:
                // print
                break;
            case POSTFIX:
                // nothing
                break;
            default:
                throw new IllegalStateException("Unknown notation: " + notation);
        }
        return VisitorResult.Continue;
    }

    @Override
    public VisitorResult visit(List<Formula> path) {
        final Notation notation = getNotation();
        switch (notation) {
            case INFIX:
                // if not literal and infixable and == 2 children
                final Formula currentNode = TreeVisitor.getCurrentNode(path);
                if (currentNode.getChildren().size() == 2) {
                    alignLine(path.size());
                    Symbols.getOperator((Formula) currentNode);
                }
                break;
            case PREFIX:
                // nothing
                break;
            case POSTFIX:
                // nothing
                break;
            default:
                throw new IllegalStateException("Unknown notation: " + notation);
        }
        return VisitorResult.Continue;
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Notation notation = getNotation();
        switch (notation) {
            case INFIX:
                // nothing
                break;
            case PREFIX:
                // nothing
                break;
            case POSTFIX:
                // print
                break;
            default:
                throw new IllegalStateException("Unknown notation: " + notation);
        }
        return VisitorResult.Continue;
    }

    
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

    
    private String variableToString(String variable) {
        return (isEnquoteWhitespace() && (containsWhitespace(variable) || equalsSymbol(variable)))
                ? '"' + variable + '"'
                : variable;
    }

    private void alignLine(int depth) {
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

    
    private boolean equalsSymbol(String s) {
        return getSymbols().parseSymbol(s) != Operator.UNKNOWN;
    }

    
    private static boolean containsWhitespace(String s) {
        return s.matches(".*?\\s+.*");
    }
}
