package evolution_impl.gpprograms.trees

import evolution_engine.evolution.PopulationInitializer
import evolution_impl.gpprograms.base.{JavaCodeIndividual, NameCounter, RandomGrowInitializer}
import evolution_impl.gpprograms.trees.HeuristicTreeIndividual

/**
 * Created by Itay on 20/04/2015.
 */
class HeuristicTreeInitializer(params: List[Any], val methodCount: Int, val depth: Int) extends PopulationInitializer[HeuristicTreeIndividual] {
  protected val heuristicInitializer = new RandomGrowInitializer(params, methodCount)
  protected val decisionInitializer = new RandomGrowInitializer(params, 1)

  override def getInitialPopulation(n: Int): List[HeuristicTreeIndividual] = (for (i <- 0 to n) yield new HeuristicTreeIndividual(growNode(depth), NameCounter.getNext.toString)).toList

  protected def growNode(depth: Int): TreeNode = {
    if (depth == 0)
      new HeuristicLeaf(heuristicInitializer.growIndividual(NameCounter.getNext))
    else
      new HeuristicNode(decisionInitializer.growIndividual(NameCounter.getNext), growNode(depth - 1), growNode(depth - 1))
  }
}
