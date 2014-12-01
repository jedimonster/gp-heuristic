package evolution_impl.gpprograms.util

import evolution_impl.gpprograms.TreeGrowingException
import evolution_impl.gpprograms.scope.CallableNode
import japa.parser.ast.`type`.ClassOrInterfaceType
import japa.parser.ast.expr.{IntegerLiteralExpr, ConditionalExpr, CastExpr}

/**
 * Created by itayaza on 01/12/2014.
 */
object TypesConversionStrategy {
  def convertTo(toType: String, n: CallableNode): CallableNode = {
    n.referenceType.toString match {
      case "double" | "int" | "java.lang.Double" =>
        toType match {
          case "double" | "int" | "java.lang.Double" => new CallableNode(new CastExpr(new ClassOrInterfaceType(toType), n.getCallStatement))
          case _ => throw new TreeGrowingException("dunno how to convert " + n.referenceType.toString + " to " + toType)
        }
      case "boolean" =>
        toType match {
          case "double" | "int" | "java.lang.Double" => new CallableNode(new ConditionalExpr(n.getCallStatement, new IntegerLiteralExpr("1"), new IntegerLiteralExpr("0")))
          case _ => throw new TreeGrowingException("dunno how to convert " + n.referenceType.toString + " to " + toType)
        }
    }

  }

  def toDouble(n: CallableNode): CallableNode = {
    n.referenceType.toString match {
      case "double" => n
      case "int" => new CallableNode(new CastExpr(new ClassOrInterfaceType("double")
        , n.getCallStatement), new ClassOrInterfaceType("double"))
    }
  }

  def canConvertTo(from: String, to: String): Boolean = {
    if (from.equals(to))
      return true
    if (to.equals("double")) {
      return from.equals("boolean") || from.equals("int")
    }
    if (to.equals("int")) {
      return from.equals("double") || from.equals("java.lang.Double")
    }
    false
  }
}
