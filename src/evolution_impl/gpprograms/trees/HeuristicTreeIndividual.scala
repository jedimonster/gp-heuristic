package evolution_impl.gpprograms.trees

import evolution_impl.fitness.dummyagent.StateObservationWrapper
import evolution_impl.gpprograms.base.{HeuristicIndividual, NameCounter}

/**
 * Created by Itay on 20/04/2015.
 */
class HeuristicTreeIndividual(val root: TreeNode, name: String) extends HeuristicIndividual {
  override def run(state: StateObservationWrapper): Double = traverseTree(root, state)

  override def getName: String = name

  override def duplicate: HeuristicTreeIndividual = new HeuristicTreeIndividual(root.duplicate, NameCounter.getNext.toString)

  override def compile(): Unit = {
    for (i <- root.inOrder)
      i.heuristic.compile()
  }

  protected def traverseTree(node: TreeNode, state: StateObservationWrapper): Double = {
    node match {
      case HeuristicLeaf(h) => h.run(state)
      case HeuristicNode(h, l, r, threshold) => if (h.run(state) <= threshold) traverseTree(l, state) else traverseTree(r, state)
    }
  }

  override def toString: String = {
    root.inOrder.map(t => t.toString).foldRight[String]("") {
      (l, r) => l + r
    }
  }
}
