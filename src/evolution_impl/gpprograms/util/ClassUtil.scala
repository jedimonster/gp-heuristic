package evolution_impl.gpprograms.util

import java.lang.reflect.{GenericArrayType, Method, Type}

import evolution_impl.gpprograms.scope.CallableNode
import japa.parser.ast.`type`.{ClassOrInterfaceType, PrimitiveType}
import japa.parser.ast.expr.{NameExpr, Expression, MethodCallExpr}
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

/**
 * Created by itayaza on 26/11/2014.
 */
object ClassUtil {
  /**
   * assumes @params are going to be named arg0,...argn and creates callables for each of their members
   * stops at iterables and primitives
   * @param params
   * @return
   */
  def extractCallables(params: List[Any], scope: Expression): Seq[CallableNode] = {
    var callables: Seq[CallableNode] = Seq()
    var i = 0

    for (param <- params) {
      val scope = new NameExpr("arg" + i)
      i += 1
      callables ++= extractCallables(param.getClass, scope)
    }

    callables
  }

  def extractCallables(method: Method, scope:  Expression): Seq[CallableNode] = {
    val genericReturnType: Type = method.getGenericReturnType
    var callables = Seq[CallableNode]()
    val methodReturnType: Class[_] = method.getReturnType
    if (methodReturnType.isPrimitive
      && !methodReturnType.getName.equalsIgnoreCase("void")) {
      callables :+= new CallableNode(new MethodCallExpr(scope, method.getName), refType = new PrimitiveType(PrimitiveType.Primitive.valueOf(methodReturnType.getName.capitalize)))
    } else if (Class.forName("java.lang.Iterable").isAssignableFrom(methodReturnType)) {
      //      callables :+= new CallableNode(new MethodCallExpr(scope, method.getName), refType = new ClassOrInterfaceType(method.getGenericReturnType.toString))
    } else if (methodReturnType.isEnum) {
      // todo
    } else {
      // todo add the object itself?
      val i = 1

      // todo recursively scan:
      genericReturnType match {
        case t : ParameterizedTypeImpl =>callables ++= extractCallables(t, new MethodCallExpr(scope, method.getName))
        case  _ =>  callables ++= extractCallables(methodReturnType, new MethodCallExpr(scope, method.getName))
      }

    }

    callables
  }

  def extractCallables(returnType: ParameterizedTypeImpl, scope: Expression): Seq[CallableNode] = {
    // todo
    Seq[CallableNode]()
  }

//  def extractCallables(returnType: Class[_], scope: => Expression): Seq[CallableNode] = {
  def extractCallables(returnType: Class[_], scope: => Expression): Seq[CallableNode] = {
    // if returnType is primitive or iterable, return a callable node for it.
    if (returnType.isPrimitive || Class.forName("java.lang.Iterable").isAssignableFrom(returnType.getClass)) {
      //      return new CallableNode(new Call)
    } else {
      // else recursively search its members
      var callables: Seq[CallableNode] = Seq[CallableNode]()
      for (method <- returnType.getMethods) {
        if (!method.getReturnType.toString.equals(returnType.toString)) { // todo we should allow some recursion
          callables ++= extractCallables(method, scope)
        }
      }

      return callables
    }
    return Seq()
  }

  def isIterable(t: Type): Boolean = {
    t match {
      case t: GenericArrayType => true
      case _ => false
    }
  }

}
