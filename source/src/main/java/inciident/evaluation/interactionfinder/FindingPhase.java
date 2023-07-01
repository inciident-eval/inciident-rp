
package inciident.evaluation.interactionfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import inciident.analysis.sat4j.CoreDeadAnalysis;
import inciident.analysis.sat4j.FastRandomConfigurationGenerator;
import inciident.analysis.sat4j.RandomConfigurationGenerator;
import inciident.analysis.sat4j.RandomConfigurationUpdater;
import inciident.clauses.CNFProvider;
import inciident.clauses.LiteralList;
import inciident.clauses.solutions.SolutionList;
import inciident.clauses.solutions.io.ListFormat;
import inciident.evaluation.EvaluationPhase;
import inciident.evaluation.Evaluator;
import inciident.evaluation.util.ModelReader;
import inciident.formula.ModelRepresentation;
import inciident.formula.io.FormulaFormatManager;
import inciident.formula.io.dimacs.DIMACSFormat;
import inciident.formula.structure.Formula;
import inciident.formula.structure.FormulaProvider.CNF;
import inciident.util.io.IO;
import inciident.util.io.csv.CSVWriter;
import inciident.util.logging.Logger;


public class FindingPhase implements EvaluationPhase {

    private List<String> algorithmNameList;

    private CSVWriter runDataWriter, modelWriter, algorithmWriter;
    private int algorithmIndex, algorithmIteration;
    private ModelRepresentation model;

    private List<LiteralList> faultyConfigs;
    private List<LiteralList> faultyInteractions, faultyInteractionsUpdated;

    private InteractionFinderEvaluator interactionFinderEvaluator;

    private int t, interactionSize, interactionCount;
    private double fpNoise;
    private double fnNoise;

    private List<LiteralList> foundInteractions;
    private LiteralList foundInteractionsMerged;
    private LiteralList foundInteractionsMergedAndUpdated;
    private long elapsedTimeInMS;
    private int verificationCounter, creationCounter;
    private Path modelPath, samplePath, outPath;
    private String modelPathString, samplePathString, outPathString;

    @Override
    public void run(Evaluator evaluator) {
        Logger.setPrintStackTrace(true);
        interactionFinderEvaluator = (InteractionFinderEvaluator) evaluator;

        modelWriter = evaluator.addCSVWriter("models.csv", "ModelID", "Name", "#Variables", "#Clauses");
        algorithmWriter = evaluator.addCSVWriter("algorithms.csv", "AlgorithmID", "Name");
        runDataWriter = evaluator.addCSVWriter(
                "runData.csv",
                "ModelID",
                "ModelIteration",
                "AlgorithmID",
                "AlgorithmIteration",
                "T",
                "InteractionSize",
                // "InteractionCount",
                "Interactions",
                "InteractionsUpdated",
                // "FPNoise",
                // "FNNoise",
                // "ConfigurationVerificationLimit",
                // "ConfigurationCreationLimit",
                // "FoundInteractions",
                // "FoundInteractionsUpdated",
                "FoundInteractionCount",
                "FoundInteractionsMerged",
                "FoundMergedUpdatedIsSubsetFaultyUpdated",
                "FaultyUpdatedIsSubsetFoundMergedUpdated",
                "FoundMergedIsSubsetFaulty",
                "FaultyIsSubsetFoundMerged",
                "FaultyIsSubsetFound",
                "FoundIsSubsetFaulty",
                "FoundLiteralsCount",
                "CorrectlyFoundLiteralsCount",
                "MissedLiteralsCount",
                "IncorrectlyFoundLiteralsCount",
                "ConfigurationVerificationCount",
                "ConfigurationCreationCount",
                "Time");

        modelWriter.setLineWriter(this::writeModel);
        algorithmWriter.setLineWriter(this::writeAlgorithm);
        runDataWriter.setLineWriter(this::writeRunData);

        final ModelReader<Formula> mr = new ModelReader<>();
        mr.setPathToFiles(interactionFinderEvaluator.modelPath);
        mr.setFormatSupplier(FormulaFormatManager.getInstance());

        if (evaluator.systemIterations.getValue() > 0) {
            evaluator.tabFormatter.setTabLevel(0);
            Logger.logInfo("Start");

            prepareAlgorithms();

            modelPath = evaluator.tempPath.resolve("model.dimacs");
            modelPathString = modelPath.toString();
            samplePath = evaluator.tempPath.resolve("sample.csv");
            samplePathString = samplePath.toString();
            outPath = evaluator.tempPath.resolve("output");
            outPathString = outPath.toString();

            systemLoop:
            for (evaluator.systemIndex = 0; evaluator.systemIndex < evaluator.systemIndexMax; evaluator.systemIndex++) {
                evaluator.tabFormatter.setTabLevel(1);
                evaluator.logSystem();

                if (!readModel(mr)) {
                    continue systemLoop;
                }

                LiteralList coreDead = model.get(new CoreDeadAnalysis());
                RandomConfigurationUpdater globalUpdater = new RandomConfigurationUpdater(model, new Random(0));

                try {
                    IO.save(model.get(CNF.fromFormula()), modelPath, new DIMACSFormat());
                } catch (IOException e) {
                    Logger.logError(e);
                    continue;
                }

                for (evaluator.systemIteration = 1;
                        evaluator.systemIteration <= evaluator.systemIterations.getValue();
                        evaluator.systemIteration++) {

                    for (Integer interactionSizeValue : interactionFinderEvaluator.interactionSizeProperty.getValue()) {
                        interactionSize = interactionSizeValue;
                        for (Integer interactionCountValue :
                                interactionFinderEvaluator.interactionCountProperty.getValue()) {
                            interactionCount = interactionCountValue;

                            Random random1 = new Random(evaluator.randomSeed.getValue() + evaluator.systemIteration);
                            faultyConfigs = model.getResult(getConfigGenerator(random1))
                                    .map(SolutionList::getSolutions)
                                    .orElse(Logger::logProblems);
                            if (faultyConfigs == null) {
                                throw new RuntimeException();
                            }
                            try {
                                IO.save(
                                        new SolutionList(model.getVariables(), faultyConfigs),
                                        samplePath,
                                        new ListFormat());
                            } catch (IOException e) {
                                Logger.logError(e);
                                continue;
                            }

                            Random random2 = new Random(evaluator.randomSeed.getValue() + evaluator.systemIteration);
                            faultyInteractions = faultyConfigs.stream()
                                    .map(c -> new LiteralList(Stream.generate(() -> (random2.nextInt(c.size()) + 1)) //
                                            .mapToInt(Integer::intValue) //
                                            .filter(l -> !coreDead.containsAnyVariable(l))
                                            .distinct() //
                                            .limit(interactionSize) //
                                            .map(l -> c.get(l - 1)) //
                                            .toArray()))
                                    .collect(Collectors.toList());
                            faultyInteractionsUpdated = faultyInteractions.stream()
                                    .map(globalUpdater::update)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .collect(Collectors.toList());

                            for (Double fpNoiseValue : interactionFinderEvaluator.fpNoiseProperty.getValue()) {
                                fpNoise = fpNoiseValue;
                                for (Double fnNoiseValue : interactionFinderEvaluator.fnNoiseProperty.getValue()) {
                                    fnNoise = fnNoiseValue;

                                    algorithmLoop:
                                    for (algorithmIndex = 0;
                                            algorithmIndex < algorithmNameList.size();
                                            algorithmIndex++) {
                                        for (Integer tValue : interactionFinderEvaluator.tProperty.getValue()) {
                                            t = tValue;

                                            for (algorithmIteration = 1;
                                                    algorithmIteration <= evaluator.algorithmIterations.getValue();
                                                    algorithmIteration++) {
                                                evaluator.tabFormatter.setTabLevel(2);
                                                logRun();
                                                evaluator.tabFormatter.setTabLevel(3);

                                                try {
                                                    startInteractionFinder(coreDead);

                                                    if (foundInteractions != null) {
                                                        foundInteractionsMerged = LiteralList.merge(
                                                                foundInteractions,
                                                                model.getFormula()
                                                                        .getVariableMap()
                                                                        .get()
                                                                        .getVariableCount());
                                                        foundInteractionsMergedAndUpdated = globalUpdater
                                                                .update(foundInteractionsMerged)
                                                                .orElse(null);
                                                    } else {
                                                        foundInteractionsMerged = null;
                                                        foundInteractionsMergedAndUpdated = null;
                                                    }

                                                    runDataWriter.writeLine();
                                                } catch (final Exception e) {
                                                    Logger.logError(e);
                                                    continue algorithmLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                evaluator.tabFormatter.setTabLevel(0);
            }
            Logger.logInfo("Finished");
        } else {
            Logger.logInfo("Nothing to do");
        }
    }

    @SuppressWarnings("unused")
    private void startInteractionFinder2(LiteralList coreDead) {
        try {
            InteractionFinderRunner.main(new String[] {
                modelPathString, //
                samplePathString, //
                outPathString, //
                interactionFinderEvaluator.algorithmsProperty.getValue().get(algorithmIndex), //
                String.valueOf(t), //
                encodeLiterals(List.of(coreDead)), //
                String.valueOf(interactionFinderEvaluator.randomSeed.getValue()
                        + interactionFinderEvaluator.systemIteration), //
                encodeLiterals(faultyInteractions), //
                String.valueOf(fpNoise), //
                String.valueOf(fnNoise)
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startInteractionFinder(LiteralList coreDead) {
        try {
            Process process = new ProcessBuilder(
                            "java", //
                            "-Xmx" + interactionFinderEvaluator.memoryProperty.getValue() + "g", //
                            "-da", //
                            "-cp", //
                            "inciident-eval-1.0.0-all.jar", //
                            "inciident.evaluation.interactionfinder.InteractionFinderRunner", //
                            modelPathString, //
                            samplePathString, //
                            outPathString, //
                            interactionFinderEvaluator
                                    .algorithmsProperty
                                    .getValue()
                                    .get(algorithmIndex), //
                            String.valueOf(t), //
                            encodeLiterals(List.of(coreDead)), //
                            String.valueOf(interactionFinderEvaluator.randomSeed.getValue()
                                    + interactionFinderEvaluator.systemIteration), //
                            encodeLiterals(faultyInteractions), //
                            String.valueOf(fpNoise), //
                            String.valueOf(fnNoise)) //
                    .start();
            BufferedReader prcErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                String[] results = Files.lines(outPath).toArray(String[]::new);
                elapsedTimeInMS = Long.parseLong(results[0]);
                creationCounter = Integer.parseInt(results[1]);
                verificationCounter = Integer.parseInt(results[2]);

                if ("null".equals(results[3])) {
                    foundInteractions = null;
                } else {
                    foundInteractions = new ArrayList<>(results.length - 3);
                    for (int i = 3; i < results.length; i++) {
                        foundInteractions.add(InteractionFinderRunner.parseLiteralList(results[i]));
                    }
                }
            } else {
                elapsedTimeInMS = -1;
                creationCounter = -1;
                verificationCounter = -1;
                foundInteractions = null;
            }
            if (prcErr.ready()) {
                String split = prcErr.lines().reduce("", (s1, s2) -> s1 + s2 + "\n");
                Logger.logError(split);
            }
            try {
                prcErr.close();
            } catch (IOException e) {
                Logger.logError(e);
            }
            process.destroyForcibly();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            Logger.logError(e);
        }
    }

    private static String encodeLiterals(List<LiteralList> literalLists) {
        StringBuilder sb = new StringBuilder();
        for (LiteralList literalList : literalLists) {
            if (!literalList.isEmpty()) {
                for (int l : literalList.getLiterals()) {
                    sb.append(l);
                    sb.append(";");
                }
                sb.replace(sb.length() - 1, sb.length(), ",");
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        } else {
            sb.append("null");
        }
        return sb.toString();
    }

    private boolean readModel(final ModelReader<Formula> mr) {
        model = mr.read(interactionFinderEvaluator.getSystemName())
                .map(ModelRepresentation::new)
                .orElse(Logger::logProblems);
        if (model == null) {
            Logger.logError("Could not read file " + interactionFinderEvaluator.getSystemName());
            return false;
        }
        modelWriter.writeLine();
        model.get(CNFProvider.fromFormula());
        return true;
    }

    protected void prepareAlgorithms() {
        algorithmNameList = new ArrayList<>();
        algorithmIndex = 0;
        for (final String algorithmName : interactionFinderEvaluator.algorithmsProperty.getValue()) {
            algorithmNameList.add(algorithmName);
            algorithmWriter.writeLine();
            algorithmIndex++;
        }
        algorithmIndex = 0;
    }

    protected void writeModel(CSVWriter modelCSVWriter) {
        modelCSVWriter.addValue(interactionFinderEvaluator.getSystemID());
        modelCSVWriter.addValue(interactionFinderEvaluator.getSystemName());
        Formula cnf = model.get(CNF.fromFormula());
        modelCSVWriter.addValue(cnf.getVariableMap().get().getVariableCount());
        modelCSVWriter.addValue(cnf.getNumberOfChildren());
    }

    protected void writeAlgorithm(CSVWriter algorithmCSVWriter) {
        algorithmCSVWriter.addValue(algorithmIndex);
        algorithmCSVWriter.addValue(algorithmNameList.get(algorithmIndex));
    }

    protected void writeRunData(CSVWriter dataCSVWriter) {
        dataCSVWriter.addValue(interactionFinderEvaluator.getSystemID());
        dataCSVWriter.addValue(interactionFinderEvaluator.systemIteration);
        dataCSVWriter.addValue(algorithmIndex);
        dataCSVWriter.addValue(algorithmIteration);

        dataCSVWriter.addValue(t);
        dataCSVWriter.addValue(interactionSize);
        // dataCSVWriter.addValue(interactionCount);
        dataCSVWriter.addValue(str(faultyInteractions));
        dataCSVWriter.addValue(str(faultyInteractionsUpdated));
        // dataCSVWriter.addValue(fpNoise);
        // dataCSVWriter.addValue(fnNoise);
        // dataCSVWriter.addValue(configVerificationLimit);
        // dataCSVWriter.addValue(configCreationLimit);

        // dataCSVWriter.addValue(str(foundInteractions));
        // dataCSVWriter.addValue(str(foundInteractionsUpdated));
        if (foundInteractions != null) {
            dataCSVWriter.addValue(foundInteractions.size());
            dataCSVWriter.addValue(str(foundInteractionsMergedAndUpdated));
            dataCSVWriter.addValue(
                    faultyInteractionsUpdated.get(0).containsAll(foundInteractionsMergedAndUpdated) ? "T" : "F");
            dataCSVWriter.addValue(
                    foundInteractionsMergedAndUpdated.containsAll(faultyInteractionsUpdated.get(0)) ? "T" : "F");
            dataCSVWriter.addValue(foundInteractionsMerged.containsAll(faultyInteractions.get(0)) ? "T" : "F");
            dataCSVWriter.addValue(faultyInteractions.get(0).containsAll(foundInteractionsMerged) ? "T" : "F");
            dataCSVWriter.addValue(
                    foundInteractions.stream().anyMatch(i -> i.containsAll(faultyInteractions.get(0))) ? "T" : "F");
            dataCSVWriter.addValue(
                    foundInteractions.stream()
                                    .anyMatch(i -> faultyInteractions.get(0).containsAll(i))
                            ? "T"
                            : "F");

            dataCSVWriter.addValue(foundInteractionsMergedAndUpdated.countNonNull());
            dataCSVWriter.addValue(faultyInteractionsUpdated
                    .get(0)
                    .retainAll(foundInteractionsMergedAndUpdated)
                    .countNonNull());
            dataCSVWriter.addValue(faultyInteractionsUpdated
                    .get(0)
                    .removeAll(foundInteractionsMergedAndUpdated)
                    .countNonNull());
            dataCSVWriter.addValue(foundInteractionsMergedAndUpdated
                    .removeAll(faultyInteractionsUpdated.get(0))
                    .countNonNull());
        } else {
            dataCSVWriter.addValue(-1);
            dataCSVWriter.addValue("null");
            dataCSVWriter.addValue("N");
            dataCSVWriter.addValue("N");
            dataCSVWriter.addValue("N");
            dataCSVWriter.addValue("N");
            dataCSVWriter.addValue("N");
            dataCSVWriter.addValue("N");
            dataCSVWriter.addValue(-1);
            dataCSVWriter.addValue(-1);
            dataCSVWriter.addValue(-1);
            dataCSVWriter.addValue(-1);
        }
        dataCSVWriter.addValue(verificationCounter);
        dataCSVWriter.addValue(creationCounter);
        dataCSVWriter.addValue(elapsedTimeInMS);
    }

    private void logRun() {
        final StringBuilder sb = new StringBuilder();
        sb.append(interactionFinderEvaluator.getSystemName());
        sb.append(" (");
        sb.append(interactionFinderEvaluator.systemIndex + 1);
        sb.append("/");
        sb.append(interactionFinderEvaluator.systemNames.size());
        sb.append(") ");
        sb.append(interactionFinderEvaluator.systemIteration);
        sb.append("/");
        sb.append(interactionFinderEvaluator.systemIterations.getValue());
        sb.append(" | ");
        sb.append(faultyInteractions);
        sb.append(" | ");
        sb.append(algorithmNameList.get(algorithmIndex));
        sb.append(" (");
        sb.append(algorithmIndex + 1);
        sb.append("/");
        sb.append(algorithmNameList.size());
        sb.append(") ");
        sb.append(algorithmIteration);
        sb.append("/");
        sb.append(interactionFinderEvaluator.algorithmIterations.getValue());
        sb.append(" | (");
        sb.append(t);
        sb.append(", ");
        sb.append(fpNoise);
        sb.append(", ");
        sb.append(fnNoise);
        sb.append(")");
        Logger.logInfo(sb.toString());
    }

    private String str(List<LiteralList> interactions) {
        StringBuilder sb = new StringBuilder();
        interactions.forEach(i -> sb.append(str(i)));
        return sb.toString();
    }

    private String str(LiteralList interaction) {
        return Arrays.toString(interaction.getLiterals());
    }

    private RandomConfigurationGenerator getConfigGenerator(Random random) {
        RandomConfigurationGenerator generator;
        generator = new FastRandomConfigurationGenerator();
        generator.setAllowDuplicates(false);
        generator.setRandom(random);
        generator.setLimit(interactionCount);
        return generator;
    }
}
