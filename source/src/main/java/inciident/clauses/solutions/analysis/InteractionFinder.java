
package inciident.clauses.solutions.analysis;

import java.util.Collection;
import java.util.List;

import inciident.clauses.LiteralList;


public interface InteractionFinder {

    public static class Statistic {
        private final int t;
        private final int interactionCounter;
        private final int creationCounter;
        private final int verifyCounter;
        private final int iterationCounter;

        public Statistic(int t, int interactionCounter, int creationCounter, int verifyCounter, int iterationCounter) {
            this.t = t;
            this.interactionCounter = interactionCounter;
            this.creationCounter = creationCounter;
            this.verifyCounter = verifyCounter;
            this.iterationCounter = iterationCounter;
        }

        public int getT() {
            return t;
        }

        public int getInteractionCounter() {
            return interactionCounter;
        }

        public int getCreationCounter() {
            return creationCounter;
        }

        public int getVerifyCounter() {
            return verifyCounter;
        }

        public int getIterationCounter() {
            return iterationCounter;
        }
    }

    LiteralList getCore();

    ConfigurationVerifyer getVerifier();

    ConfigurationUpdater getUpdater();

    void setCore(LiteralList core);

    void setUpdater(ConfigurationUpdater updater);

    void setVerifier(ConfigurationVerifyer verifier);

    void setConfigurationVerificationLimit(int configurationVerificationLimit);

    void setConfigurationCreationLimit(int configurationCreationLimit);

    void addConfigurations(Collection<LiteralList> configurations);

    List<LiteralList> find(int t);

    List<Statistic> getStatistics();

    List<LiteralList> getSample();

    void reset();
}
