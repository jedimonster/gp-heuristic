package evolution_impl.mutators

import japa.parser.ast.expr.DoubleLiteralExpr

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class ConstantVisitor[A] extends ASTVisitor[A] {

  override def visit(n: DoubleLiteralExpr, arg: A): Unit = {
    val diff: Long = Math.round(Math.random() * 5) // keep the sign, another mutator will change that
    n.setValue((n.getValue.toDouble + diff).toString)
  }
}
