package evolution_impl.gpprograms.scope

import evolution_impl.gpprograms.util.{TypesConversionStrategy, ClassUtil}
import japa.parser.ast.Node
import japa.parser.ast.body.VariableDeclaratorId

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
 * Created by itayaza on 03/11/2014.
 */
class Scope(val node: Node, parentScope: Scope = null) {
  var callables: ListBuffer[CallableNode] = ListBuffer()
  var childScopes: ListBuffer[Scope] = ListBuffer()

  def addCallable(callable: CallableNode) = {
    callables = callables :+ callable
  }

  def addCallables(toadd: mutable.Iterable[CallableNode]) = {
    callables = callables ++ toadd
  }

  def addChildScope(scope: Scope) = {
    childScopes = childScopes :+ scope
  }

  //  def getCallablesByType(t: String): ListBuffer[CallableNode] = {
  //    val parentCallables = if (parentScope == null) ListBuffer() else parentScope.getCallablesByType(t)
  //    callables.filter(c => c.referenceType.toString.equals(t)) ++ parentCallables
  //  }

  def getCallablesByType(t: String, filter: CallableNode => Boolean = _ => true): ListBuffer[CallableNode] = {
    val parentCallables = if (parentScope == null) ListBuffer() else parentScope.getCallablesByType(t)
    var res = ListBuffer[CallableNode]()
    for (c <- callables if filter(c)) {
      //      if (c.referenceType.toString.equals(t)) {
      if (TypesConversionStrategy.canConvertTo(c.referenceType.toString, t)) {
        res +:= c
      }
      else if (!c.referenceType.toString.equals("int") && !c.referenceType.toString.equals("double") && !c.referenceType.toString.equals("boolean")) {
        val expandedCallables: Seq[CallableNode] = ClassUtil.extractCallables(Class.forName(c.referenceType.toString), c.getCallStatement)
        // todo apparently at this point ^ c might not have its parameters satisfied.
        res ++= expandedCallables.filter(c => c.referenceType.toString.equals(t))
      }
    }
    res
  }

}
