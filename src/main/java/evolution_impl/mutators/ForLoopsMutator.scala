package evolution_impl.mutators

//import java.util

import bgu.cs.evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.base.JavaCodeIndividual
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
 * Created by itayaza on 04/01/2015.
 */
class ForLoopsMutator(probability: Double) extends Mutator[JavaCodeIndividual] {

  override def getProbability: Double = probability

  override def mutate(individuals: java.util.List[JavaCodeIndividual]): java.util.List[JavaCodeIndividual] = {
    val inds: List[JavaCodeIndividual] = individuals.toList
    for (individual: JavaCodeIndividual <- inds) {
      //      new ConstantVisitor().visit(individual.ast, null)
      val visitor: ASTVisitor[JavaCodeIndividual] = new ForLoopsVisitor(probability)
      visitor.visit(individual.ast, individual)
    }
    inds.asJava
  }
}

