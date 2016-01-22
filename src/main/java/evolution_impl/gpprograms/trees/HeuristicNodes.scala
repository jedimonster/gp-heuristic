package evolution_impl.gpprograms.trees

import evolution_impl.gpprograms.base.JavaCodeIndividual

/**
 * Created by Itay on 20/04/2015.
 */
abstract class TreeNode(var heuristic: JavaCodeIndividual) {
  def duplicate: TreeNode

  def inOrder: List[TreeNode]

  override def toString: String = heuristic.toString
}

case class HeuristicNode(h: JavaCodeIndividual, left: TreeNode, right: TreeNode, var threshold: Double = 0.0) extends TreeNode(h) {
  def duplicate: HeuristicNode = new HeuristicNode(h.duplicate, left.duplicate, right.duplicate)

  def inOrder: List[TreeNode] = left.inOrder ::: List(this) ::: right.inOrder

  override def toString: String = "Threshold=" + threshold + super.toString
}

case class HeuristicLeaf(h: JavaCodeIndividual) extends TreeNode(h) {
  def duplicate: HeuristicLeaf = new HeuristicLeaf(h.duplicate)

  def inOrder: List[TreeNode] = List(this)
}
