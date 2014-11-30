package evolution_impl.gpprograms.scope

import evolution_impl.gpprograms.TreeGrowingException
import japa.parser.ast.Node
import japa.parser.ast.`type`.{ClassOrInterfaceType, Type}
import japa.parser.ast.body.{MethodDeclaration, Parameter}
import japa.parser.ast.expr._

import scalaj.collection.Imports._

/**
 * Created by itayaza on 03/11/2014.
 */
class CallableNode(val node: Node, val refType: Type = null) {
  val referenceType: Type = {
    if (refType == null) {
      node match {
        case c: MethodDeclaration => c.getType
        case v: VariableDeclarationExpr => v.getType
        case p: Parameter => p.getType
        case e: Expression => new ClassOrInterfaceType("double")
        case _ => throw new TreeGrowingException("Tried to create callable node of unknown type")
      }
    } else {
      refType
    }
  }
  var assignments: Map[Parameter, Expression] = Map()
  var parameters: Seq[Parameter] = {
    node match {
      case c: MethodDeclaration => c.getParameters.asScala
      case _ => List()
    }
  }

  @throws[TreeGrowingException]("if trying to set a none-existing parameter")
  def setParameter(parameter: Parameter, cu: CallableNode) = {
    if (parameters.contains(parameter)) {
      // assign or reassign the parameter
      assignments += (parameter -> cu.getCallStatement)
    } else {
      // can't find that parameter anywhere in the dependency lists
      throw new TreeGrowingException("Parameter" + parameter + " not found in node's dependency list")
    }
  }

  def getUnsatisfiedParameters: Seq[Parameter] = parameters.filterNot((p: Parameter) => assignments.contains(p))

  def getCallStatement: Expression = {
    node match {
      case m: MethodDeclaration =>
        val params: Seq[Expression] = for (param <- parameters) yield assignments.get(param) match {
          case Some(e: Expression) => e
          case None => throw new TreeGrowingException("Tried to call method without assigning all paramaters")
        }
        new MethodCallExpr(null, m.getName, params.asJava)
      case v: VariableDeclarationExpr => v.getVars.get(0).getInit // we assume there's only one var defined at a time!
      case p: Parameter => new NameExpr(p.getId.getName)
      case e: Expression => e
    }
  }

  //    override def toString() = getCallStatement.toString
}
