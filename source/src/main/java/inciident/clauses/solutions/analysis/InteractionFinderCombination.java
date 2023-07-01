
package inciident.clauses.solutions.analysis;

import java.util.Collection;
import java.util.List;

import inciident.clauses.LiteralList;

public abstract class InteractionFinderCombination implements InteractionFinder {

    protected final AInteractionFinder finder;

    public InteractionFinderCombination(AInteractionFinder finder) {
        this.finder = finder;
    }

    @Override
    public List<Statistic> getStatistics() {
        return finder.getStatistics();
    }

    @Override
    public List<LiteralList> getSample() {
        return finder.getSample();
    }

    @Override
    public void setConfigurationVerificationLimit(int configurationVerificationLimit) {
        finder.setConfigurationVerificationLimit(configurationVerificationLimit);
    }

    @Override
    public void setConfigurationCreationLimit(int configurationCreationLimit) {
        finder.setConfigurationCreationLimit(configurationCreationLimit);
    }

    @Override
    public ConfigurationUpdater getUpdater() {
        return finder.getUpdater();
    }

    @Override
    public ConfigurationVerifyer getVerifier() {
        return finder.getVerifier();
    }

    @Override
    public void setUpdater(ConfigurationUpdater updater) {
        finder.setUpdater(updater);
    }

    @Override
    public void setVerifier(ConfigurationVerifyer verifier) {
        finder.setVerifier(verifier);
    }

    @Override
    public LiteralList getCore() {
        return finder.getCore();
    }

    @Override
    public void setCore(LiteralList core) {
        finder.setCore(core);
    }

    @Override
    public void reset() {
        finder.reset();
    }

    @Override
    public void addConfigurations(Collection<LiteralList> configurations) {
        finder.addConfigurations(configurations);
    }
}
