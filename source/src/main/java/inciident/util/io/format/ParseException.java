
package inciident.util.io.format;


public class ParseException extends Exception {

    private static final long serialVersionUID = -5320389242364406936L;

    private final int lineNumber;

    public ParseException(String message) {
        this(message, -1);
    }

    public ParseException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
