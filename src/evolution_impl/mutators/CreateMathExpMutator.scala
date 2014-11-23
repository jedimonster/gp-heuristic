package evolution_impl.mutators

import java.util

import evolution_engine.mutators.Mutator
import evolution_impl.gpprograms.JavaCodeIndividual
import evolution_impl.gpprograms.scope.{Scope, ScopeManager}
import japa.parser.ast.body.MethodDeclaration
import scalaj.collection.Imports._

/**
 * creates math expression of the form x = y <op> z randomly where possible. 
 */
class CreateMathExpMutator(val probability: Double) extends Mutator[JavaCodeIndividual] {
  override def mutate(individuals: util.List[JavaCodeIndividual]): util.List[JavaCodeIndividual] = {
    for (individual: JavaCodeIndividual <- individuals.asScala) {
      if (Math.random < probability)
        mutateOne(individual)
    }

    individuals
  }

  def mutateOne(individual: JavaCodeIndividual) = {
    val scopeManager = new ScopeManager
    scopeManager.visit(individual.ast, null)
    new MathExpVisitor().visit(individual.ast, scopeManager)
  }

  override def getProbability: Double = probability
}

class MathExpVisitor extends ASTVisitor[ScopeManager] {
  // put it in the beginning of the method.
  override def visit(n: MethodDeclaration, scopeManager: ScopeManager): Unit = {
    val scope: Scope = scopeManager.getScopeByNode(n)

    // we need 3 callables of numeric type
    val callableDoubles: Any = scope.getCallablesByType("java.lang.Double")


    // continue traversing
    super.visit(n, scopeManager)
  }
}
