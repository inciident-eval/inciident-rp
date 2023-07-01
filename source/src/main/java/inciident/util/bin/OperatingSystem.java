
package inciident.util.bin;

public final class OperatingSystem {

    public static final boolean IS_WINDOWS;
    public static final boolean IS_MAC;
    public static final boolean IS_UNIX;
    public static final String HOME_DIRECTORY;

    static {
        final String osName = System.getProperty("os.name").toLowerCase();
        IS_WINDOWS = osName.matches(".*(win).*");
        IS_MAC = osName.matches(".*(mac).*");
        IS_UNIX = osName.matches(".*(nix|nux|aix).*");
        HOME_DIRECTORY = System.getProperty("user.home");
    }
}
