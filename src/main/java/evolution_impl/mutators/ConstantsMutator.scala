package evolution_impl.mutators

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.base.JavaCodeIndividual
import japa.parser.ast.expr.{BinaryExpr, DoubleLiteralExpr}
import org.apache.commons.math3.distribution.{LevyDistribution, LogNormalDistribution, NormalDistribution}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Random

/**
 * Created By Itay Azaria
 * Date: 9/17/2014
 */
class ConstantsMutator(probability: Double) extends Mutator[JavaCodeIndividual] {

  override def getProbability: Double = probability

  override def mutate(individuals: util.List[JavaCodeIndividual]): util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (x: JavaCodeIndividual <- inds) {
      //      new ConstantVisitor().visit(x.ast, null)
      val constantVisitor: ConstantVisitor[Object] = new ConstantVisitor[Object](probability)
      val signVisitor: SignVisitor[Object] = new SignVisitor[Object]()
      constantVisitor.visit(x.ast, new Object)
      signVisitor.visit(x.ast, new Object)
    }
    inds.asJava
  }
}

class ConstantVisitor[A](probability: Double) extends ASTVisitor[A] {
  //  val dist = new LogNormalDistribution(0,2)
//    val dist = new NormalDistribution(0, 1)
  val dist = new LevyDistribution(0, 0.5)

  override def visit(n: DoubleLiteralExpr, arg: A): Unit = {
    //    val diff: Double = Math.round(Math.random()) // keep the sign, another mutator will change that
    var diff: Double = dist.sample // keep the sign, another mutator will change that
    if (Math.random() > 0.5)
      diff *= -1
    if (n.getValue.toDouble.signum != (n.getValue.toDouble + diff).signum)
      diff = -1 * diff

    if (Math.random < this.probability)
      n.setValue((n.getValue.toDouble + diff).toString)
    super.visit(n, arg)
  }
}

//class ConstantVisitor[A](probability: Double) extends ASTVisitor[A] {
//  val dist = new NormalDistribution(0, 1)
//
//  override def visit(n: DoubleLiteralExpr, arg: A): Unit = {
//    //    val diff: Double = Math.round(Math.random()) // keep the sign, another mutator will change that
//    val diff = dist.sample()
//    if (Math.random < this.probability) {
//      val oldVal: Double = n.getValue.toDouble
//      var newVal: Double = oldVal + diff
//      if (newVal.signum != oldVal.signum)
//        newVal = -1 * newVal
//      n.setValue(newVal.toString)
//    }
//    super.visit(n, arg)
//  }
//}


class SignVisitor[T]() extends ASTVisitor[T] {
  //  override def visit(n: DoubleLiteralExpr, arg: T): Unit = {
  //    if (Math.random() < 0.5)
  //      n.setValue((-1 * n.getValue.toDouble).toString)
  //    super.visit(n, arg)
  //  }

  override def visit(n: BinaryExpr, arg: T): Unit = {
    if (n.getOperator.equals(BinaryExpr.Operator.minus)
      && Math.random() < 0.1)
      n.setOperator(BinaryExpr.Operator.plus)
    else if (n.getOperator.equals(BinaryExpr.Operator.plus)
      && Math.random() < 0.1)
      n.setOperator(BinaryExpr.Operator.minus)
    //    if (Random.nextBoolean)
    //      n.setOperator(BinaryExpr.Operator.times)
    //    else
    //      n.setOperator(BinaryExpr.Operator.divide)
    super.visit(n, arg)
  }
}
