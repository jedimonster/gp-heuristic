package evolution_impl.mutators

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.JavaCodeIndividual
import japa.parser.ast.expr.DoubleLiteralExpr

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class ConstantsMutator(probability: Double) extends Mutator[JavaCodeIndividual] {

  override def getProbability: Double = probability

  override def mutate(individuals: util.List[JavaCodeIndividual]): util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (x: JavaCodeIndividual <- inds if Math.random() < probability) {
      //      new ConstantVisitor().visit(x.ast, null)
      val visitor: ConstantVisitor[Object] = new ConstantVisitor[Object]()
      val signVisitor: SignVisitor[Object] = new SignVisitor[Object]()
      visitor.visit(x.ast, new Object)
    }
    inds.asJava
  }
}

class ConstantVisitor[A] extends ASTVisitor[A] {

  override def visit(n: DoubleLiteralExpr, arg: A): Unit = {
    val diff: Long = Math.round(Math.random() * 5) // keep the sign, another mutator will change that
    if (math.random < 0.3)
      n.setValue((n.getValue.toDouble + diff).toString)
    super.visit(n, arg)
  }
}


class SignVisitor[T]() extends ASTVisitor[T] {
  override def visit(n: DoubleLiteralExpr, arg: T): Unit = {
    if (Math.random() < 0.05)
      n.setValue((-1 * n.getValue.toDouble).toString)
    super.visit(n, arg)
  }
}
