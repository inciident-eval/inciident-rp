
package inciident.evaluation;

import inciident.util.extension.ExtensionPoint;


public class EvaluationPhaseExtensionPoint extends ExtensionPoint<EvaluationPhase> {

    private static EvaluationPhaseExtensionPoint instance = new EvaluationPhaseExtensionPoint();

    public static EvaluationPhaseExtensionPoint getInstance() {
        return instance;
    }
}
