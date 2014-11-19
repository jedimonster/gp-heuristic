package evolution_engine;

import evolution_engine.evolution.Individual;
import evolution_engine.fitness.FitnessResult;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * Created by itayaza on 27/10/2014.
 */
public class CSVEvolutionLogger implements EvolutionLogger {
    private final Path logDir;
    private final Path fitnessFile;
    private final Path statsFile;
    protected int generation = 0;

    private CSVEvolutionLogger(Path logDir, Path fitnessFile, Path statsFile) {
        this.logDir = logDir;
        this.fitnessFile = fitnessFile;
        this.statsFile = statsFile;
    }

    public static CSVEvolutionLogger createCSVEvolutionLogger(Path logDir) throws IOException {
        Files.createDirectories(logDir);
        Files.createDirectories(logDir.resolve("individuals/"));
        File fitnessFile = new File(logDir.toFile(), "fitness.csv");
        File statsFile = new File(logDir.toFile(), "stats.csv");
        fitnessFile.createNewFile();
        statsFile.createNewFile();
        Path statsPath = statsFile.toPath();

        Files.write(statsPath, "Mean,Min,Max,Std. Dev\n".getBytes(), new OpenOption[]{APPEND});

        return new CSVEvolutionLogger(logDir, fitnessFile.toPath(), statsPath);
    }

    @Override
    public void addGeneration(List<? extends Individual> individuals, FitnessResult fitness) {
        addGenerationStatistics(individuals, fitness);
        writeIndividualToDisk(individuals);
        try {
            StringBuilder line = new StringBuilder();
            OpenOption[] options = new OpenOption[]{APPEND};

            for (Individual individual : individuals) {
                line.append(fitness.getFitness(individual)).append(",");
            }

            line.append("\n");
            Files.write(fitnessFile, line.toString().getBytes(StandardCharsets.UTF_8), options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        generation++;
    }

    private void writeIndividualToDisk(List<? extends Individual> individuals) {
        try {
            Path generationDir = logDir.resolve("individuals/" + generation + "/");
            Files.createDirectories(generationDir);
            for (Individual individual : individuals) {
                String name = individual.getName();
                Path individualPath = generationDir.resolve(name + ".java");
                Files.write(individualPath, individual.toString().getBytes(StandardCharsets.UTF_8), CREATE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void addGenerationStatistics(List<? extends Individual> individuals, FitnessResult fitness) {
        double[] fitnessArray = new double[individuals.size()];
        int i = 0;
        for (Individual individual : individuals) {
            fitnessArray[i++] = fitness.getFitness(individual);
        }
        DescriptiveStatistics stats = new DescriptiveStatistics(fitnessArray);

        try {
            OpenOption[] options = new OpenOption[]{APPEND};
            String line = String.format("Average = %f, Minimum = %f, Maximum = %f, Std. dev. = %f\n", stats.getMean(), stats.getMin(), stats.getMax(), stats.getStandardDeviation());
            System.out.println(line);

            Files.write(statsFile, line.toString().getBytes(StandardCharsets.UTF_8), options);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
