package evolution_impl.mutators.trees

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.fitness.IndividualHolder
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.NameCounter
import evolution_impl.gpprograms.trees.{HeuristicNode, TreeNode}
import scala.util.Random

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


/**
 * Created by Itay on 23/04/2015.
 */
class NodeThresholdMutator(probability: Double) extends Mutator[HeuristicTreeIndividual] {
  /**
   * mutates the given features according to their fitness and appropriate strategy.
   *
   * @param individuals map of haar features and their fitness (between 0 to 1)
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
    val nodes: List[TreeNode] = individual.root.inOrder.filter(tn => tn.isInstanceOf[HeuristicNode])
    val victimNode = nodes(Random.nextInt(nodes.size))

    val newValue: Double = victimNode.heuristic.run(new StateObservationWrapper(IndividualHolder.currentState))
    victimNode.asInstanceOf[HeuristicNode].threshold = newValue
  }

  override def getProbability: Double = probability
}
