package evolution_engine.algorithm;

import evolution_engine.evolution.EvolutionStrategy;
import evolution_engine.fitness.FitnessCalculator;
import evolution_engine.mutators.Crossover;
import evolution_engine.mutators.Mutator;
import evolution_engine.selection.RankSelectionStrategy;
import evolution_engine.selection.SelectionStrategy;

import java.awt.*;

/**
 * Created By Itay Azaria
 * Date: 10/10/13
 */
public class Config {
    public static final int NUMBER_OF_CLUSTERS = 8;
    public static final double SELECTION_PRESSURE = 1.5;
    public static int FRAME_WIDTH = 96;
    public static int FRAME_HEIGHT = 96;
    public static final int SCALE_METHOD = Image.SCALE_SMOOTH;
    public static final double[] IMAGE_SCALES = {1};

    public static final FitnessCalculator FITNESS_CALCULATOR = null;
    public static final EvolutionStrategy EVOLUTION_STRATEGY =  null;
    public static final SelectionStrategy SELECTION_STRATEGY = new RankSelectionStrategy();


    public static final String UNPROCESSED_IMAGES_DIR = "Data\\rectangles-triangles";


//        public static final String DATA_DIRECTORY = "Data\\barrels-butterflies";
//        public  static final String DATA_DIRECTORY = "Data\\SimpleShapes";
//        public static final String DATA_DIRECTORY = "Data\\AB";
//        public static final String DATA_DIRECTORY = "Data\\pizza-pyramid";
//        public static final String DATA_DIRECTORY = "Data\\bb2";
//        public static final String DATA_DIRECTORY = "Data\\some-cats-some-asians";
    public static final String DATA_DIRECTORY = "Data\\casettes-tanks";
//    public static final String DATA_DIRECTORY = "Data\\rectangles-triangles";

    public static final int FEATURE_HOPP_DISTANCE = 10;

    public static String LOG_DIRECTORY = "C:\\Users\\Itay\\Documents\\av_logs\\";

    public static final String FILE_EXTENSIONS = ".*\\.(jpg|jpeg|png|PNG|JPG)";

    public static final double IMAGE_WIDTH = 256;

    public static final int POPULATION_SIZE = 64;
    public static int GENERATIONS = 50;

//    public static final double P_MUTATE = 0.6;
//    public static final double P_CROSSOVER = 0.3;

//    public static Logger log = null;


    // for positioned features:
    public static Mutator mutators[] = {    };
    public static Crossover crossovers[] = {};


    public static final int SUBSET_SIZE = 4;
    public static final int SUBSET_COUNT = 8; //how many subsets an individual has to be in for fitness calculations

    @Override
    public String toString() {
        String res;
        res = "Config:\r\n";
        res += "Selection Strategy: " + SELECTION_STRATEGY.getClass();
        res += ",\r\nEvolution Strategy: " + EVOLUTION_STRATEGY.getClass();
        res += ",\r\nFitness CalculAtor: " + FITNESS_CALCULATOR.getClass();
        res += String.format("\r\n,Frame Dimensions: %d x %d,\r\n" +
                "Data Dir: %s,\r\n" +
                "Population Size: %d,\r\n" +
                "Generations: %d,\r\n\r\n" +
                "Mutators:\r\n", FRAME_WIDTH, FRAME_HEIGHT, DATA_DIRECTORY, POPULATION_SIZE, GENERATIONS);
        for (Mutator mutator : mutators) {
            res += String.format("\t" +
                    "P(%s)=%f,\r\n", mutator.getClass().toString(), mutator.getProbability());
        }
        res += "\r\nCrossover Operators:\r\n";
        for (Crossover crossover : crossovers) {
            res += String.format("\tP(%s)=%f,\r\n", crossover.getClass().toString(), crossover.getProbability());
        }


        return res;
    }
}
