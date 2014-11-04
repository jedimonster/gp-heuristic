package evolution_impl.gpprograms.scope

import evolution_impl.gpprograms
import evolution_impl.gpprograms.scope
import japa.parser.ast.Node
import japa.parser.ast.body.{ClassOrInterfaceDeclaration, MethodDeclaration, Parameter}
import japa.parser.ast.expr.{ConditionalExpr, MethodCallExpr, NameExpr, VariableDeclarationExpr}
import japa.parser.ast.stmt._
import japa.parser.ast.visitor.VoidVisitorAdapter

import scala.collection.mutable


/**
 * Created by itayaza on 02/11/2014.
 */

class ScopeManager extends VoidVisitorAdapter[scope.Scope] {
  val scopeByNode = new mutable.HashMap[Node, scope.Scope]()

  def getScopeByNode(k: Node) = scopeByNode.get(k) match {
    case Some(e) => e
    case None => new scope.Scope(k)
  }

  override def visit(n: MethodDeclaration, parentScope: scope.Scope): Unit = {
    //      if (parentScope != null) {
    parentScope.addCallable(new gpprograms.scope.CallableNode(n))
    //      }
    // create a new scope for the method
    val scope = new gpprograms.scope.Scope(n, parentScope)

    // add method's parameters to the scope
    //      val value: Any = for (p <- n.getParameters) yield new CallableNode(p)
    //      scope.addCallables(value)

    // bind the scope to the method
    scopeByNode.put(n, scope)

    // continue traversing
    super.visit(n, scope)
  }

  //
  override def visit(n: ClassOrInterfaceDeclaration, parentScope: scope.Scope): Unit = {
    val scope = new gpprograms.scope.Scope(n, parentScope)
    scopeByNode.put(n, scope)
    super.visit(n, scope)
  }

  override def visit(n: Parameter, arg: scope.Scope): Unit = {
    arg.addCallable(new scope.CallableNode(n))
    super.visit(n, arg)
  }

  override def visit(n: ReturnStmt, parentScope: scope.Scope): Unit = {
    // no new scopes, just bind it
    scopeByNode.put(n, parentScope)
    super.visit(n, parentScope)
  }

  override def visit(n: ConditionalExpr, arg: Scope): Unit = {
    createBindNewScope(n, arg)
    super.visit(n, arg)
  }

  override def visit(n: ForeachStmt, arg: Scope): Unit = {
    createBindNewScope(n, arg)
    super.visit(n, arg)
  }

  override def visit(n: ForStmt, arg: Scope): Unit = {
    createBindNewScope(n, arg)
    super.visit(n, arg)
  }

  override def visit(n: IfStmt, arg: Scope): Unit = {
    createBindNewScope(n, arg)
    super.visit(n, arg)
  }

  protected def createBindNewScope(n: Node, arg: Scope) {
    val newScope = new Scope(n, arg)
    scopeByNode.put(n, arg)
  }

  override def visit(n: MethodCallExpr, arg: Scope): Unit = {
    createBindNewScope(n, arg)
    super.visit(n, arg)
  }

  override def visit(n: NameExpr, arg: Scope): Unit = {
    createBindNewScope(n, arg)
    super.visit(n, arg)
  }

  override def visit(n: VariableDeclarationExpr, arg: Scope): Unit = {
    createBindNewScope(n, arg)
    super.visit(n, arg)
  }
}

