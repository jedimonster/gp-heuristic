package evolution_impl.mutators

import japa.parser.ast.body.MethodDeclaration
import japa.parser.ast.visitor.VoidVisitorAdapter

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class ASTVisitor[A] extends VoidVisitorAdapter[A] {
  override def visit(n: MethodDeclaration, arg: A): Unit =
    if (!n.getName.equals("main") && !n.getName.equals("run"))
      super.visit(n, arg)
}
