
package inciident.evaluation;

import inciident.util.extension.Extension;


public interface EvaluationPhase extends Extension {

    default String getName() {
        return getIdentifier();
    }

    void run(Evaluator evaluator) throws Exception;
}
