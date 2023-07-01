
package inciident.analysis.solver;

public class RuntimeTimeoutException extends RuntimeException {

    private static final long serialVersionUID = -6922001608864037759L;

    public RuntimeTimeoutException() {
        super();
    }

    public RuntimeTimeoutException(String message) {
        super(message);
    }

    public RuntimeTimeoutException(Throwable cause) {
        super(cause);
    }

    public RuntimeTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
