package evolution_engine;

import evolution_engine.evolution.Individual;
import evolution_engine.fitness.FitnessResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * Created by itayaza on 27/10/2014.
 */
public class CSVEvolutionLogger implements EvolutionLogger {
    private final Path logDir;
    private final Path fitnessFile;

    private CSVEvolutionLogger(Path logDir, Path fitnessFile) {
        this.logDir = logDir;
        this.fitnessFile = fitnessFile;
    }

    public static CSVEvolutionLogger createCSVEvolutionLogger(Path logDir) throws IOException {
        Files.createDirectories(logDir);
        File fitnessFile = new File(logDir.toFile(), "fitness.csv");
        fitnessFile.createNewFile();

        return new CSVEvolutionLogger(logDir, fitnessFile.toPath());
    }

    @Override
    public void addGeneration(List<? extends Individual> individuals, FitnessResult fitness) {
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


    }
}
