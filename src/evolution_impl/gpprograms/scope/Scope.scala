package evolution_impl.gpprograms.scope

import japa.parser.ast.Node

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

  def getCallablesByType(t: String): ListBuffer[CallableNode] = {
    val parentCallables = if (parentScope == null) ListBuffer() else parentScope.getCallablesByType(t)
    callables.filter(c => c.referenceType.toString.equals(t)) ++ parentCallables
  }

}
