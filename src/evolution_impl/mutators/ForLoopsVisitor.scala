package evolution_impl.mutators

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.JavaCodeIndividual
import evolution_impl.gpprograms.scope.{Scope, ScopeManager, CallableNode}
import evolution_impl.gpprograms.util.ClassUtil
import japa.parser.ast.`type`.ClassOrInterfaceType
import japa.parser.ast.body.{VariableDeclaratorId, VariableDeclarator, MethodDeclaration}
import japa.parser.ast.expr.AssignExpr.Operator
import japa.parser.ast.expr._
import japa.parser.ast.stmt._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Random

/**
 * Created by Itay on 24/03/2015.
 */

class ForLoopsVisitor(probability: Double) extends ASTVisitor[JavaCodeIndividual] {
  override def visit(method: MethodDeclaration, arg: JavaCodeIndividual): Unit = {
    super.visit(method, arg)
    if (Math.random() > probability)
      return

    method.getBody.setStmts(
      method.getBody.getStmts.filterNot(
        s => s.isInstanceOf[ForeachStmt]
          || (s.isInstanceOf[ExpressionStmt] && s.asInstanceOf[ExpressionStmt].getExpression.isInstanceOf[VariableDeclarationExpr] && s.asInstanceOf[ExpressionStmt].getExpression.asInstanceOf[VariableDeclarationExpr].getVars.get(0).getId.toString.equals("acc"))))
    // pick a random Iterable from expandedParams:
    val iterables: Seq[CallableNode] = arg.gardener.get.expandedParams.filter(can => can.referenceType.toString.startsWith("java.lang.Iterable"))
    if (iterables.size == 0)
      return

    val iterable = iterables(Random.nextInt(iterables.size))
    val typePattern = "\\<(.*)\\>".r
    val typeStrMatch = typePattern findFirstMatchIn iterable.referenceType.toString
    //    val typeClass = Class.forName(typeStrMatch)
    var innerScope: Seq[CallableNode] = Seq[CallableNode]()
    val typeStr: String = typeStrMatch.get.group(1)
    if (ForLoopsVisitor.expandedTypes.containsKey(typeStr)) {
      innerScope = ForLoopsVisitor.expandedTypes(typeStr)
    } else {
      // we need to satisfy callables from scope
      // todo we might we to do it even in the first case of the if since we can have new ways to satisfy callables -> new options
      arg.gardener.get.satisfyCallables(List[CallableNode](iterable), List[CallableNode]())
      // todo error if iterable not in result of statement
      innerScope = ClassUtil.extractCallables(Class.forName(typeStr), new NameExpr("item")).filter(can => can.parametersSatisfied && can.referenceType.toString.equals("double"))
    }

    val statements: ListBuffer[Statement] = ListBuffer[Statement]()
    val innerStatements: ListBuffer[Statement] = ListBuffer[Statement]()

    val accVar: VariableDeclarator = new VariableDeclarator(new VariableDeclaratorId("acc"), new DoubleLiteralExpr("0.0"))
    statements.append(new ExpressionStmt(new VariableDeclarationExpr(new ClassOrInterfaceType("double"), List(accVar))))
    val allowedOperators = List(Operator.plus, Operator.minus, Operator.star, Operator.slash)
    if (Math.random() > 0.5)
      innerStatements.append(new ExpressionStmt(new AssignExpr(new NameExpr("acc"), innerScope(Random.nextInt(innerScope.length)).getCallStatement, allowedOperators(Random.nextInt(allowedOperators.size)))))
    else {
      val randomMathBinrayOperator: String = if (Random.nextBoolean()) "max" else "min"
      val minExpr: MethodCallExpr = new MethodCallExpr(new NameExpr("Math"), randomMathBinrayOperator, List(innerScope(Random.nextInt(innerScope.length)).getCallStatement, new NameExpr("acc")).asJava)
      innerStatements.append(new ExpressionStmt(new AssignExpr(new NameExpr("acc"), minExpr, Operator.assign)))
    }

    val bodyStatements = new BlockStmt(innerStatements.asJava)
    val forStatement = new ForeachStmt(
      new VariableDeclarationExpr(
        new ClassOrInterfaceType(typeStr),
        List(new VariableDeclarator(new VariableDeclaratorId("item")))),
      iterable.getCallStatement,
      bodyStatements)

    statements.append(forStatement)
    method.getBody.getStmts.addAll(statements.asJava)

    val scopeManager = new ScopeManager()
    //    scopeManager.visit(method, null)
    method.getBody.accept(scopeManager, new Scope(method))
    val scope: Scope = scopeManager.getScopeByNode(method.getBody.getStmts.last)

    var nodesToReturn: ListBuffer[CallableNode] = scope.getCallablesByType("double").filter(can => can.parametersSatisfied)
    //    nodesToReturn = Random.shuffle(nodesToReturn).slice(0, 4)

    arg.gardener.get.createReturnStatement(method, nodesToReturn.toList, List[CallableNode](), true)
  }
}
object ForLoopsVisitor {
  val expandedTypes = new mutable.HashMap[String, Seq[CallableNode]]()
}