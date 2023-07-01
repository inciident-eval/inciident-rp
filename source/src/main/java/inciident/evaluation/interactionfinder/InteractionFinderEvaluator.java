
package inciident.evaluation.interactionfinder;

import inciident.evaluation.Evaluator;
import inciident.evaluation.properties.ListProperty;
import inciident.evaluation.properties.Property;

public class InteractionFinderEvaluator extends Evaluator {

    Property<Integer> memoryProperty = new Property<>("memory", Property.IntegerConverter, 8);
    ListProperty<Integer> tProperty = new ListProperty<>("t", Property.IntegerConverter);
    ListProperty<Integer> interactionSizeProperty = new ListProperty<>("interactionSize", Property.IntegerConverter);
    ListProperty<Integer> interactionCountProperty = new ListProperty<>("interactionCount", Property.IntegerConverter);
    ListProperty<Double> fpNoiseProperty = new ListProperty<>("fpNoise", Property.DoubleConverter);
    ListProperty<Double> fnNoiseProperty = new ListProperty<>("fnNoise", Property.DoubleConverter);
    ListProperty<Integer> configVerificationLimitProperty =
            new ListProperty<>("configVerificationLimit", Property.IntegerConverter);
    ListProperty<Integer> configCreationLimitProperty =
            new ListProperty<>("configCreationLimit", Property.IntegerConverter);
    ListProperty<String> algorithmsProperty = new ListProperty<>("algorithm", Property.StringConverter);

    @Override
    public String getName() {
        return "interaction-finder";
    }
}
