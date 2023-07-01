
package inciident.util.bin;

import inciident.util.extension.ExtensionPoint;


public class BinaryManager extends ExtensionPoint<Binary> {

    private static final BinaryManager instance = new BinaryManager();

    public static BinaryManager getInstance() {
        return instance;
    }
}
