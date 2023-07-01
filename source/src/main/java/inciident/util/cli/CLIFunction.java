
package inciident.util.cli;

import java.util.List;

import inciident.util.extension.Extension;


public interface CLIFunction extends Extension {

    default String getName() {
        return getIdentifier();
    }

    void run(List<String> args);

    default String getDescription() {
        return "";
    }

    default String getHelp() {
        return "No help is available for this command.";
    }
}
