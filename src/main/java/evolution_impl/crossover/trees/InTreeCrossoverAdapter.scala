package evolution_impl.crossover.trees

import evolution_engine.mutators.Crossover
import evolution_impl.crossover.JavaCodeCrossover
import evolution_impl.gpprograms.base.JavaCodeIndividual

import scala.util.Random

/**
 * Created by Itay on 20/04/2015.
 */
class InTreeCrossoverAdapter(crossover: JavaCodeCrossover, probability: Double) extends Crossover[HeuristicTreeIndividual] {

  override def getProbability: Double = probability

  override def cross(father: HeuristicTreeIndividual, mother: HeuristicTreeIndividual): List[HeuristicTreeIndividual] = {
    // duplicate 2 parents
    val son = father.duplicate
    val daugther = father.duplicate

    // pick a random node from each
    val fatherHeuristics = son.root.inOrder
    val motherHeuristics = daugther.root.inOrder

    // then cross them, in place.
    val fatherNode = fatherHeuristics(Random.nextInt(fatherHeuristics.size))
    val motherNode = motherHeuristics(Random.nextInt(motherHeuristics.size))
    val childHeuristics: List[JavaCodeIndividual] = crossover.cross(fatherNode.heuristic, motherNode.heuristic)
    fatherNode.heuristic = childHeuristics(0)
    motherNode.heuristic = childHeuristics(1)

    List(son, daugther)
  }

//  override def cross[A >: HeuristicTreeIndividual](father: HeuristicTreeIndividual, mother: HeuristicTreeIndividual): List[HeuristicTreeIndividual] = {
//    // duplicate 2 parents
//    val son = father.duplicate
//    val daugther = father.duplicate
//
//    // pick a random node from each
//    val fatherHeuristics = son.root.inOrder
//    val motherHeuristics = daugther.root.inOrder
//
//    // then cross them, in place.
//    val fatherNode = fatherHeuristics(Random.nextInt(fatherHeuristics.size))
//    val motherNode = motherHeuristics(Random.nextInt(motherHeuristics.size))
//    val childHeuristics: List[JavaCodeIndividual] = crossover.cross(fatherNode.heuristic, motherNode.heuristic)
//    fatherNode.heuristic = childHeuristics(0)
//    motherNode.heuristic = childHeuristics(1)
//
//    List(son, daugther)
//  }
}
