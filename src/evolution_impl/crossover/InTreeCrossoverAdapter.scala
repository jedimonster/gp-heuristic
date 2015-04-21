package evolution_impl.crossover

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.trees.{HeuristicTreeIndividual, TreeNode}
import scalaj.collection.Imports._

/**
 * Created by Itay on 20/04/2015.
 */
class InTreeCrossoverAdapter(crossover: JavaCodeCrossover, probability: Double) extends Mutator[HeuristicTreeIndividual] {
  /**
   * mutates the given features according to their fitness and appropriate strategy.
   *
   * @param individuals map of haar features and their fitness (between 0 to 1)
   * @return list of the mutated haar features
   */
  override def mutate(individuals: util.List[HeuristicTreeIndividual]): util.List[HeuristicTreeIndividual] = {
    for (individual <- individuals.asScalaMutable) {
      if (Math.random < probability)
        crossRandomNode(individual.root)
    }
    individuals
  }

  override def getProbability: Double = probability

  protected def crossRandomNode(node: TreeNode, depth: Int = 0) = {

  }
}
