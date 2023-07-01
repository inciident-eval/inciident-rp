
package inciident.util.job;


public interface InternalMonitor extends Monitor {

    public static class MethodCancelException extends RuntimeException {

        public MethodCancelException() {
            super("Method was canceled");
        }

        private static final long serialVersionUID = 1L;
    }

    
    void setTotalWork(long work);

    
    void step() throws MethodCancelException;

    
    void step(long work) throws MethodCancelException;

    void uncertainStep() throws MethodCancelException;

    void uncertainStep(long work) throws MethodCancelException;

    InternalMonitor subTask(int size);

    void setTaskName(String name);

    
    void checkCancel() throws MethodCancelException;

    void done();
}
