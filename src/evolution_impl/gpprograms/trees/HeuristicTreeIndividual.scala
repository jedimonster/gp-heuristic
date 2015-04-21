package evolution_impl.gpprograms.trees

import evolution_engine.evolution.Individual
import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.{NameCounter, HeuristicIndividual}

/**
 * Created by Itay on 20/04/2015.
 */
class HeuristicTreeIndividual(val root: TreeNode, name: String) extends HeuristicIndividual {
  override def run(state: StateObservationWrapper): Double = traverseTree(root, state)

  override def getName: String = name

  override def duplicate: HeuristicTreeIndividual = new HeuristicTreeIndividual(root.duplicate, NameCounter.next.toString)

  protected def traverseTree(node: TreeNode, state: StateObservationWrapper): Double = {
    node match {
      case HeuristicLeaf(h) => h.run(state)
      case HeuristicNode(h, l, r) => if (h.run(state) > 0) traverseTree(l, state) else traverseTree(r, state)
    }
  }
}
