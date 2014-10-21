package evolution_engine.selection;

import evolution_engine.algorithm.Config;
import evolution_engine.evolution.Individual;
import evolution_engine.fitness.FitnessResult;

import java.util.*;

/**
 * Created By Itay Azaria
 * Date: 4/6/2014
 */
public class RankSelectionStrategy<I extends Individual> implements SelectionStrategy<I> {
    @Override
    public List<I> select(List<I> previousGeneration, FitnessResult fitness) {
        ArrayList<I> selectedFeatures = new ArrayList<>(Config.POPULATION_SIZE);
        List<I> allFeatures;
        allFeatures = sortFeaturesByFitness(previousGeneration, fitness);
        int N = allFeatures.size();
        double[] probabilities = new double[N];
        final double SP = Config.SELECTION_PRESSURE;
        double sum = 0;

//        List<HaarFeatureInt> elites = new ArrayList<>();
//        for (int i = 0; i < Math.log(Config.POPULATION_SIZE); i++) {
//            elites.add(allFeatures.get(i));
//        }


        for (int i = 0; i < N; i++) {
            double rank = N - i - 1;

            double p = ((2 - SP) / N) + ((2 * rank) * (SP - 1)) / (N * (N - 1));
            probabilities[i] = probabilities[Math.max(0, i - 1)] + p;
            sum += probabilities[i];
        }
         probabilities[N - 1] = 1.0;

//        selectedFeatures.addAll(elites);
        while (selectedFeatures.size() < previousGeneration.size()) {
            double p = Math.random();
            for (int i = 0; i < probabilities.length; i++) {
                double current = probabilities[i];
                if (p < current) {
                    I e = (I) allFeatures.get(i).duplicate();
                    selectedFeatures.add(e);
                    break;
                }
            }
        }

        return selectedFeatures;
    }

    public List<I> sortFeaturesByFitness(List<I> features, final FitnessResult<I> fitnessResult) {
        Collections.sort(features, new Comparator<I>() {
            @Override
            public int compare(I o1, I o2) {
//                return (int) (1000 * (fitnessResult.getFitness(o2) - fitnessResult.getFitness(o1)));
                return (int) (1000 * (fitnessResult.getFitness(o1) - fitnessResult.getFitness(o2))); // minimize
            }
        });
        for (int i = 0; i < features.size(); i++) {
            System.out. printf("feature %d = %f\n", i, fitnessResult.getFitness(features.get(i)));
        }
        return features;
    }


}
