package evolution_impl.gpprograms.trees

import evolution_engine.evolution.PopulationInitializer
import evolution_impl.gpprograms.base.{JavaCodeIndividual, NameCounter, RandomGrowInitializer}
import evolution_impl.gpprograms.trees.HeuristicTreeIndividual

/**
 * Created by Itay on 20/04/2015.
 */
class HeuristicTreeInitializer(params: List[Any], val methodCount: Int, val depth: Int) extends PopulationInitializer[HeuristicTreeIndividual] {
  protected val heuristicInitializer = new RandomGrowInitializer(params, 2)

  override def getInitialPopulation(n: Int): List[HeuristicTreeIndividual] = (for (i <- 0 to n) yield new HeuristicTreeIndividual(growNode(depth), NameCounter.getNext.toString)).toList

  protected def growNode(depth: Int): TreeNode = {
    val nodeHeuristic: JavaCodeIndividual = heuristicInitializer.growIndividual(NameCounter.getNext)
    if (depth == 0)
      new HeuristicLeaf(nodeHeuristic)
    else
      new HeuristicNode(nodeHeuristic, growNode(depth - 1), growNode(depth - 1))
  }
}
