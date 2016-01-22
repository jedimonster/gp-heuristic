package evolution_impl.gpprograms

import japa.parser.ast.body.{Parameter, MethodDeclaration}
import scala.collection.JavaConversions._

/**
 * Created by itayaza on 23/11/2014.
 */
class MethodSignature(method: MethodDeclaration) {
  val returnType = method.getType
  val paramTypes = {
    for (param: Parameter <- method.getParameters.toSet) yield param.getType
  }


  def canEqual(other: Any): Boolean = other.isInstanceOf[MethodSignature]

  override def equals(other: Any): Boolean = other match {
    case that: MethodSignature =>
      (that canEqual this) &&
        returnType.equals(that.returnType) &&
        paramTypes.equals(that.paramTypes)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(returnType, paramTypes)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
