
package inciident.analysis.solver;


public class RuntimeContradictionException extends RuntimeException {

    private static final long serialVersionUID = -4951752949650801254L;

    public RuntimeContradictionException() {
        super();
    }

    public RuntimeContradictionException(String message) {
        super(message);
    }

    public RuntimeContradictionException(Throwable cause) {
        super(cause);
    }

    public RuntimeContradictionException(String message, Throwable cause) {
        super(message, cause);
    }
}
