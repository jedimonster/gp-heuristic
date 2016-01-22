package evolution_impl.mutators.trees

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.base.NameCounter
import evolution_impl.gpprograms.trees.{HeuristicTreeIndividual, TreeNode}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Random
/**
 * Created By Itay Azaria
 * Date: 4/22/2015
 */
class RegrowHeuristicMutator(probability: Double) extends Mutator[HeuristicTreeIndividual] {
  /**
   * mutates the given features according to their fitness and appropriate strategy.
   *
   * @param features map of haar features and their fitness (between 0 to 1)
   * @return list of the mutated haar features
   */
  override def mutate(individuals: util.List[HeuristicTreeIndividual]): util.List[HeuristicTreeIndividual] = {
    val inds: List[HeuristicTreeIndividual] = individuals.toList
    for (individual <- inds) {
      //      new ConstantVisitor().visit(individual.ast, null)
      if (Math.random < probability) {
        mutateOne(individual)
      }
    }
    inds.asJava
  }

  def mutateOne(individual: HeuristicTreeIndividual) = {
    val nodes: List[TreeNode] = individual.root.inOrder
    val victimNode = nodes(Random.nextInt(nodes.size))
    victimNode.heuristic = victimNode.heuristic.gardener.get.growIndividual(NameCounter.getNext)
  }

  override def getProbability: Double = probability
}
