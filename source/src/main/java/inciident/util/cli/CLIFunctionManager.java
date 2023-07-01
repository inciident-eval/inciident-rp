
package inciident.util.cli;

import inciident.util.extension.ExtensionPoint;


public class CLIFunctionManager extends ExtensionPoint<CLIFunction> {

    private static CLIFunctionManager instance = new CLIFunctionManager();

    public static CLIFunctionManager getInstance() {
        return instance;
    }
}
