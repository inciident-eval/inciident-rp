
package inciident.evaluation;

import java.io.IOException;
import java.nio.file.Files;

import inciident.util.logging.Logger;


public class OutputCleaner implements EvaluationPhase {

    @Override
    public void run(Evaluator evaluator) throws IOException {
        Files.deleteIfExists(evaluator.outputRootPath.resolve(".current"));
        Logger.logInfo("Reset current output path.");
    }

    @Override
    public String getName() {
        return "clean";
    }
}
