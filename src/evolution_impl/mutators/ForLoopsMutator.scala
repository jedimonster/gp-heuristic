package evolution_impl.mutators

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.JavaCodeIndividual
import evolution_impl.gpprograms.scope.CallableNode
import evolution_impl.gpprograms.util.ClassUtil
import japa.parser.ast.body.MethodDeclaration
import japa.parser.ast.expr.{NameExpr, DoubleLiteralExpr}
import japa.parser.ast.stmt.Statement

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Random

/**
 * Created by itayaza on 04/01/2015.
 */
class ForLoopsMutator(probability: Double) extends Mutator[JavaCodeIndividual] {

  override def getProbability: Double = probability

  override def mutate(individuals: util.List[JavaCodeIndividual]): util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (individual: JavaCodeIndividual <- inds) {
      //      new ConstantVisitor().visit(individual.ast, null)
      val visitor: ASTVisitor[JavaCodeIndividual] = new ForLoopsVisitor(probability)
      visitor.visit(individual.ast, individual)
    }
    inds.asJava
  }
}

class ForLoopsVisitor(probability: Double) extends ASTVisitor[JavaCodeIndividual] {
  override def visit(method: MethodDeclaration, arg: JavaCodeIndividual): Unit = {
    super.visit(method, arg)
    if (Math.random() > probability)
      return

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
      // todo we might we to do it even in the first case of the if
      arg.gardener.get.satisfyCallables(List[CallableNode](iterable), List[CallableNode]())
      // todo error if iterable not in result of stmnt
      innerScope = ClassUtil.extractCallables(Class.forName(typeStr), new NameExpr("item")).filter(can => can.parametersSatisfied && can.referenceType.toString.equals("double"))
    }
    print(innerScope)
    val statements: ListBuffer[Statement] = ListBuffer[Statement]()

  }
}

object ForLoopsVisitor {
  val expandedTypes = new mutable.HashMap[String, Seq[CallableNode]]()
}