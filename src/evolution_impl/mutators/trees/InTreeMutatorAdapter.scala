package evolution_impl.mutators.trees

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.base.JavaCodeIndividual
import evolution_impl.gpprograms.trees.HeuristicTreeIndividual
import scalaj.collection.Imports._

/**
 * Created By Itay Azaria
 * Date: 4/22/2015
 */
class InTreeMutatorAdapter(probability: Double, mutators: List[Mutator[JavaCodeIndividual]])
  extends Mutator[HeuristicTreeIndividual] {

  override def mutate(features: util.List[HeuristicTreeIndividual]): util.List[HeuristicTreeIndividual] = {
    // pick trees to undergo mutation according to our probability
    val chosenTrees = features.asScalaMutable.filter(p => Math.random() < probability)
    for (mutator <- mutators) {
      for (tree <- chosenTrees) {
        mutator.mutate(tree.root.inOrder.map(node => node.heuristic).asJava)
      }
    }
    features
  }

  override def getProbability: Double = probability
}
