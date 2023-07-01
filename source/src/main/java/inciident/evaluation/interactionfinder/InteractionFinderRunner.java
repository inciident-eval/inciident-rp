
package inciident.evaluation.interactionfinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import inciident.analysis.sat4j.RandomConfigurationUpdater;
import inciident.clauses.LiteralList;
import inciident.clauses.solutions.analysis.InteractionFinder;
import inciident.clauses.solutions.analysis.InteractionFinder.Statistic;
import inciident.clauses.solutions.analysis.InteractionFinderCombinationForwardBackward;
import inciident.clauses.solutions.analysis.InteractionFinderWrapper;
import inciident.clauses.solutions.analysis.NaiveRandomInteractionFinder;
import inciident.clauses.solutions.analysis.SingleInteractionFinder;
import inciident.clauses.solutions.io.ListFormat;
import inciident.formula.ModelRepresentation;
import inciident.util.extension.ExtensionLoader;
import inciident.util.io.IO;
import inciident.util.logging.Logger;


public class InteractionFinderRunner {

    public static void main(String[] args) throws IOException {
        ExtensionLoader.load();

        ModelRepresentation model = ModelRepresentation.load(Paths.get(args[0])).orElse(Logger::logProblems);
        List<LiteralList> sample = IO.load(Paths.get(args[1]), new ListFormat())
                .orElse(Logger::logProblems)
                .getSolutions();
        Path outputPath = Paths.get(args[2]);
        InteractionFinder algorithm = parseAlgorithm(args[3]);
        int t = Integer.parseInt(args[4]);
        LiteralList core = parseLiteralList(args[5]);
        Long seed = Long.parseLong(args[6]);

        List<LiteralList> interactions = Arrays.stream(args[7].split(","))
                .map(InteractionFinderRunner::parseLiteralList)
                .collect(Collectors.toList());

        Double fpNoise = Double.parseDouble(args[8]);
        Double fnNoise = Double.parseDouble(args[9]);

        algorithm.reset();
        algorithm.setCore(core);
        algorithm.setVerifier(new ConfigurationOracle(interactions, fpNoise, fnNoise));
        algorithm.setUpdater(new RandomConfigurationUpdater(model, new Random(seed)));
        algorithm.addConfigurations(sample);

        long startTime = System.nanoTime();
        List<LiteralList> foundInteractions = algorithm.find(t);
        long endTime = System.nanoTime();

        List<Statistic> statistics = algorithm.getStatistics();
        Statistic lastStatistic = statistics.get(statistics.size() - 1);
        long elapsedTimeInMS = (endTime - startTime) / 1_000_000;

        StringBuilder sb = new StringBuilder();
        sb.append(elapsedTimeInMS);
        sb.append("\n");
        sb.append(lastStatistic.getCreationCounter());
        sb.append("\n");
        sb.append(lastStatistic.getVerifyCounter());
        sb.append("\n");
        if (foundInteractions != null) {
            for (LiteralList foundInteraction : foundInteractions) {
                for (int l : foundInteraction.getLiterals()) {
                    sb.append(l);
                    sb.append(";");
                }
                sb.replace(sb.length() - 1, sb.length(), "\n");
            }
            sb.delete(sb.length() - 1, sb.length());
        } else {
            sb.append("null");
        }
        Files.writeString(outputPath, sb.toString());
    }

    public static LiteralList parseLiteralList(String arg) {
        return ("null".equals(arg))
                ? null
                : new LiteralList(Arrays.stream(arg.split(";"))
                        .mapToInt(Integer::parseInt)
                        .toArray());
    }

    private static InteractionFinder parseAlgorithm(String algorithm) {
        InteractionFinder interactionFinderRandom;
        switch (algorithm) {
            case "NaiveRandom": {
                interactionFinderRandom = new InteractionFinderWrapper(new NaiveRandomInteractionFinder(), true, false);
                break;
            }
            case "Single": {
                interactionFinderRandom = new InteractionFinderWrapper(new SingleInteractionFinder(), true, false);
                break;
            }
            case "IterativeNaiveRandom": {
                interactionFinderRandom = new InteractionFinderWrapper(new NaiveRandomInteractionFinder(), true, true);
                break;
            }
            case "IterativeSingle": {
                interactionFinderRandom = new InteractionFinderWrapper(new SingleInteractionFinder(), true, true);
                break;
            }
            case "ForwardBackward": {
                interactionFinderRandom = new InteractionFinderCombinationForwardBackward();
                break;
            }
            default:
                interactionFinderRandom = null;
        }
        return interactionFinderRandom;
    }
}
