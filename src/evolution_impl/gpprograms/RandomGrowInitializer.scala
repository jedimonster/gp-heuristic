package evolution_impl.gpprograms

import java.io.File
import java.lang.reflect.{ParameterizedType, Type, Method}
import evolution_engine.evolution.{EvolutionParameters, PopulationInitializer}
import evolution_impl.gpprograms.scope.{CallableNode, Scope, ScopeManager}
import evolution_impl.gpprograms.util.{TypesConversionStrategy, ClassUtil}
import japa.parser.JavaParser
import japa.parser.ast.`type`.{ClassOrInterfaceType, ReferenceType}
import japa.parser.ast.body._
import japa.parser.ast.expr.{BinaryExpr, DoubleLiteralExpr}
import japa.parser.ast.stmt.{BlockStmt, ReturnStmt, Statement}
import japa.parser.ast.{CompilationUnit, Node}
import org.apache.commons.math3.distribution.NormalDistribution
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

import scala.collection.JavaConversions._
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by itayaza on 28/10/2014
 * Also known as "The Gardener"
 * Randomly grows methodCount methods that randomly use parameters from the given list of parameters
 * Combines into a linear combination of resulting numbers.
 */
class RandomGrowInitializer(params: List[Any], val methodCount: Int) extends PopulationInitializer[JavaCodeIndividual] with JavaIndividualActions {
  val distribution = new NormalDistribution(0, 5)


  val paramTypes: List[Parameter] = {
    var i = -1
    //    var types: ListBuffer[Parameter] = ListBuffer()
    //    for (callable <- ClassUtil.extractCallables(params, null)) {
    //      yield new Parameter(callable.referenceType, )
    //      ???
    //    }

    //    types.toList
    for (p <- params) yield {
      i += 1
      new Parameter(new ClassOrInterfaceType(p.getClass.getName), new VariableDeclaratorId(("arg" + i).toString))
    }
  }

  val expandedParams: Seq[CallableNode] = ClassUtil.extractCallables(params, null)
  //  println("Callables = " + expandedParams.toString)
  //  }

  val prototype: JavaCodeIndividual = {
    val prototypeFile: File = new File("individuals/Prototype.java")
    val ast: CompilationUnit = JavaParser.parse(prototypeFile)
    new JavaCodeIndividual(ast, prototypeFile)
  }

  override def getInitialPopulation(n: Int): List[JavaCodeIndividual] = {
    val individuals: Seq[JavaCodeIndividual] = for (i <- 0 to n) yield growIndividual(i)
    individuals.toList // convert to java list
  }


  def growIndividual(id: Int): JavaCodeIndividual = {

    // get a copy of the prototype
    val individual: JavaCodeIndividual = prototype.duplicate() match {
      case i: JavaCodeIndividual => i
      case _ => throw new TreeGrowingException("Individual type not supported")
    }
    // change its name
    individual.setName("Ind" + id.toString)
    individual.gardener = Some(this)

    // randomly grow methods
    val methods: IndexedSeq[MethodDeclaration] = for (i <- 0 to methodCount) yield growMethod(i, 3)
    val classDeceleration: ClassOrInterfaceDeclaration = individual.ast.getTypes.get(0) match {
      case e: ClassOrInterfaceDeclaration => e
      case _ => throw new TreeGrowingException("Can't find main class")
    }
    classDeceleration.getMembers.addAll(methods)

    // grow a run method (entry point for our gp)
    growRunMethod(individual)
    //    val runMethod = new MethodDeclaration(1, new ReferenceType(returnType), "run", ListBuffer(paramTypes: _*))
    //    runMethod.setBody(new BlockStmt(new java.util.ArrayList[Statement]()))
    //    // add it
    //    classDeceleration.getMembers.add(runMethod)


    individual
  }

  def removeRunMethod(individual: JavaCodeIndividual) = {
    val classDeceleration = individual.ast.getTypes.get(0)

    classDeceleration.setMembers(classDeceleration.getMembers.filter {
      case m: MethodDeclaration => !m.getName.equals("run")
      case _ => true
    })
  }

  def growRunMethod(individual: JavaCodeIndividual): Unit = {
    val returnType: ClassOrInterfaceType = new ClassOrInterfaceType("java.lang.Double")

    val classDeceleration: ClassOrInterfaceDeclaration = individual.ast.getTypes.get(0) match {
      case e: ClassOrInterfaceDeclaration => e
      case _ => throw new TreeGrowingException("Can't find main class")
    }

    // drop any existing run methods.
    removeRunMethod(individual)

    // grow new one.
    val runMethod = new MethodDeclaration(1, new ReferenceType(returnType), "run", ListBuffer(paramTypes: _*))
    runMethod.setBody(new BlockStmt(new java.util.ArrayList[Statement]()))
    // add it
    classDeceleration.getMembers.add(runMethod)

    val scopeManager: ScopeManager = new ScopeManager()
    scopeManager.visit(classDeceleration, null)
    createReturnStatement(runMethod, scopeManager, n => (n.node != runMethod), randomFactor = false) // create a return statement not which does not include recursive calls.
  }

  def growMethod(id: Int, paramCount: Int): MethodDeclaration = {
    val modifiers = 1
    val methodType = new ReferenceType(new ClassOrInterfaceType("java.lang.Double"))
    val name = "ADF" + id
    //    var parameters: List[Parameter] = Random.shuffle(paramTypes).slice(0, paramCount) // todo select paramCount params from the list in the field.
    var i = -1
    var parameters = Random.shuffle(expandedParams).slice(0, paramCount).map(ca => {
      i += 1
      new Parameter(ca.referenceType, new VariableDeclaratorId("arg" + i))
    })
    val method = new MethodDeclaration(modifiers, methodType, name, ListBuffer(parameters: _*))
    val scopeManager = new ScopeManager()
    method.setBody(new BlockStmt(new java.util.ArrayList[Statement]())) // create an empty body to avoid nulls in the future.
    scopeManager.visit(method, null)

    createReturnStatement(method, scopeManager)

    method
  }

  /**
   * create or overwrite the return statement in @param{method} with a new statement that uses all available local vars.
   * @param method
   * @return
   */
  def createReturnStatement(method: MethodDeclaration, scopeManager: ScopeManager, callableFilter: (CallableNode => Boolean) = (_ => true), randomFactor: Boolean = true) = {
    val node: Node = method.getBody.getStmts.size() match {
      case 0 => method // if it's empty it's new
      case _ => method.getBody.getStmts.last // otherwise it must have a return statement.
    }
    // find innermost scope
    val scope: Scope = scopeManager.getScopeByNode(node)

    // get callables relevant according to filter if any
    val callables: ListBuffer[CallableNode] = scope.getCallablesByType("java.lang.Double", callableFilter)
    callables ++= scope.getCallablesByType("int", callableFilter)

    val (satisfied, unsatisfied): (ListBuffer[CallableNode], ListBuffer[CallableNode]) = callables.partition(n => n.getUnsatisfiedParameters.size == 0)
    flatSatisfyCallables(satisfied, unsatisfied)

    // future me: I'm sorry. this folds the list into one big recursive binary expression, do not try to debug this. it works.
    // if you decide to debug this anyway, first make sure the list of satisfied callables is what you think it is.
    // then note bigExpr is the recursive BinaryExpr with the list items encored so far
    // and currentNode is the current list item to be folded into the big binary expression.
    val retExp: CallableNode = callables.foldLeft(new CallableNode(new DoubleLiteralExpr(distribution.sample.toString))) { (bigExpr: CallableNode, currentNode: CallableNode) =>
      if (randomFactor) {
        new CallableNode(new BinaryExpr(
          new BinaryExpr(
            new DoubleLiteralExpr(distribution.sample.toString),
            currentNode.getCallStatement,
            BinaryExpr.Operator.times),
          bigExpr.getCallStatement,
          BinaryExpr.Operator.plus))
      } else {
        new CallableNode(
          new BinaryExpr(
            currentNode.getCallStatement,
            bigExpr.getCallStatement,
            BinaryExpr.Operator.plus
          )
        )
      }
    }

    // add created return statement

    method.getBody.getStmts.add(new ReturnStmt(retExp.getCallStatement))
  }

  protected def flatSatisfyCallables(satisfied: ListBuffer[CallableNode], unsatisfied: ListBuffer[CallableNode]) {
    for (n: CallableNode <- unsatisfied) {
      for (up <- n.getUnsatisfiedParameters) {
        //        val potentialAssignments: ListBuffer[CallableNode] = satisfied.filter(p => p.referenceType.toString.equals(up.getType.toString))
        val potentialAssignments: ListBuffer[CallableNode] = satisfied.filter(p => TypesConversionStrategy.canConvertTo(p.referenceType.toString, up.getType.toString))
        val assignment = potentialAssignments.get(Random.nextInt(potentialAssignments.size))
        n.setParameter(up, assignment)
      }
    }
    for (n: CallableNode <- unsatisfied) {
      if (n.getUnsatisfiedParameters.size == 0)
        satisfied.add(n)
    }
  }

}
