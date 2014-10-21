package evolution_impl.mutators

import japa.parser.ast.expr.{NameExpr, IntegerLiteralExpr}
import japa.parser.ast.visitor.VoidVisitorAdapter

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class ConstantVisitor[A] extends ASTVisitor[A] {

  override def visit(n: IntegerLiteralExpr, arg: A): Unit = {
    val diff: Long = Math.round(Math.random() * 10 - 5)
    n.setValue((n.getValue.toInt + diff).toString)
  }
}
