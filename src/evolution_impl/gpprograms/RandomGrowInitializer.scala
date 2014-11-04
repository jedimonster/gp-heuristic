package evolution_impl.gpprograms

import java.io.File
import java.util

import evolution_engine.evolution.PopulationInitializer
import evolution_impl.gpprograms.scope.{CallableNode, Scope, ScopeManager}
import japa.parser.JavaParser
import japa.parser.ast.`type`.{ClassOrInterfaceType, ReferenceType}
import japa.parser.ast.body._
import japa.parser.ast.expr.{BinaryExpr, DoubleLiteralExpr}
import japa.parser.ast.stmt.{BlockStmt, ReturnStmt, Statement}
import japa.parser.ast.{CompilationUnit, Node}

import scala.collection.JavaConversions._
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable.ListBuffer
import scala.util.Random

/**
 * Created by itayaza on 28/10/2014.
 * Randomly grows methodCount methods that randomly use parameters from the given list of parameters
 * Combines into a linear combination of resulting numbers.
 */
class RandomGrowInitializer(params: List[Any], methodCount: Int) extends PopulationInitializer[JavaCodeIndividual] {
  val paramTypes: List[Parameter] = {
    var i = -1
    for (p <- params) yield {
      i += 1
      new Parameter(new ClassOrInterfaceType(p.getClass.getName), new VariableDeclaratorId(("arg" + i).toString))
    }
  }
  val prototype: JavaCodeIndividual = {
    val prototypeFile: File = new File("individuals/Prototype.java")
    val ast: CompilationUnit = JavaParser.parse(prototypeFile)
    new JavaCodeIndividual(ast, prototypeFile)
  }

  override def getInitialPopulation: java.util.List[JavaCodeIndividual] = {
    val individuals: Seq[JavaCodeIndividual] = for (i <- 0 to 100) yield growIndividual(i)
    ListBuffer(individuals: _*) // convert to java list
  }

  def growIndividual(id: Int): JavaCodeIndividual = {
    val returnType: ClassOrInterfaceType = new ClassOrInterfaceType("java.lang.Double")

    // get a copy of the prototype
    val individual: JavaCodeIndividual = prototype.duplicate() match {
      case i: JavaCodeIndividual => i
      case _ => throw new TreeGrowingException("Individual type not supported")
    }
    // change its name
    individual.setName("Ind" + id.toString)

    // randomly grow methods
    val methods: IndexedSeq[MethodDeclaration] = for (i <- 0 to methodCount) yield growMethod(i, 1)
    val classDeceleration: ClassOrInterfaceDeclaration = individual.ast.getTypes.get(0) match {
      case e: ClassOrInterfaceDeclaration => e
      case _ => throw new TreeGrowingException("Can't find main class")
    }
    classDeceleration.getMembers.addAll(methods)

    // grow a run method (entry point for our gp)
    val runMethod = new MethodDeclaration(1, new ReferenceType(returnType), "run", ListBuffer(paramTypes: _*))
    runMethod.setBody(new BlockStmt(new util.ArrayList[Statement]()))
    // add it
    classDeceleration.getMembers.add(runMethod)

    // use those methods in the main return statement
    val scopeManager: ScopeManager = new ScopeManager()
    scopeManager.visit(classDeceleration, null)
    createReturnStatement(runMethod, scopeManager)

    individual
  }

  def growMethod(id: Int, paramCount: Int): MethodDeclaration = {
    val modifiers = 1
    val methodType = new ReferenceType(new ClassOrInterfaceType("java.lang.Double"))
    val name = "ADF" + id
    val parameters: List[Parameter] = Random.shuffle(paramTypes).slice(0, paramCount) // todo select paramCount params from the list in the field.
    val method = new MethodDeclaration(modifiers, methodType, name, ListBuffer(parameters: _*))

    method
  }

  /**
   * create or overwrite the return statement in @param{method} with a new statement that uses all available local vars.
   * @param method
   * @return
   */
  def createReturnStatement(method: MethodDeclaration, scopeManager: ScopeManager, callableFilter: (CallableNode => Boolean) = (_ => true)) = {
    val node: Node = method.getBody.getStmts.size() match {
      case 0 => method
      case _ => method.getBody.getStmts.last
    }
    // find innermost scope
    val scope: Scope = scopeManager.getScopeByNode(node)

    // get callables relevant according to filter if any
    val callables: ListBuffer[CallableNode] = scope.getCallablesByType("java.lang.Double").filter(callableFilter)

    val (satisfied, unsatisfied): (ListBuffer[CallableNode], ListBuffer[CallableNode]) = callables.partition(n => n.getUnsatisfiedParameters.size == 0)
    flatSatisfyCallables(satisfied, unsatisfied)

    // create return statements from callables
    val retExp: CallableNode = callables.foldLeft(new CallableNode(new DoubleLiteralExpr((0.0).toString))) { (l: CallableNode, r: CallableNode) =>
      new CallableNode(new BinaryExpr(l.getCallStatement, r.getCallStatement, BinaryExpr.Operator.plus))
    }
    // add created return statement

    method.getBody.getStmts.add(new ReturnStmt(retExp.getCallStatement))
  }

  protected def flatSatisfyCallables(satisfied: ListBuffer[CallableNode], unsatisfied: ListBuffer[CallableNode]) {
    for (n: CallableNode <- unsatisfied) {
      for (up <- n.getUnsatisfiedParameters) {
        val potentialAssignments: ListBuffer[CallableNode] = satisfied.filter(p => p.referenceType.equals(up.getType))
        val assignment = potentialAssignments.get(Random.nextInt(potentialAssignments.size))
        n.setParameter(up, assignment)
      }
      // if it's now satisfied, move it!

      if (n.getUnsatisfiedParameters.size == 0)
        satisfied.add(n)
    }
  }
}
