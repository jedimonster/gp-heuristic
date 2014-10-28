package evolution_impl.gpprograms

import japa.parser.ast.Node
import japa.parser.ast.body.{MethodDeclaration, Parameter}
import japa.parser.ast.expr.{Expression, MethodCallExpr}

import scalaj.collection.Imports._

/**
 * Created by itayaza on 28/10/2014.
 */
class CallableNode(node: Node) {
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
      assignments += (parameter -> cu)
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
      // todo variables etc?
    }
  }
}
