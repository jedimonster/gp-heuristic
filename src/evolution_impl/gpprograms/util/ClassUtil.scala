package evolution_impl.gpprograms.util

import java.lang.annotation.Annotation
import java.lang.reflect.{Method, Type, Modifier, Field, GenericArrayType}

import evolution_impl.fitness.dummyagent.GPIgnore
import evolution_impl.gpprograms.scope.CallableNode
import japa.parser.ast.`type`.{ClassOrInterfaceType, PrimitiveType}
import japa.parser.ast.body.{MethodDeclaration, VariableDeclaratorId, Parameter}
import japa.parser.ast.expr._
import sun.reflect.generics.reflectiveObjects.{GenericArrayTypeImpl, ParameterizedTypeImpl}
import scalaj.collection.Imports._

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

  def extractCallables(method: Method, scope: Expression): Seq[CallableNode] = {
    val genericReturnType: Type = method.getGenericReturnType
    var callables = Seq[CallableNode]()
    val methodReturnType: Class[_] = method.getReturnType

    if (method.isAnnotationPresent(classOf[GPIgnore]) || !Modifier.isPublic(method.getModifiers)) {
      return callables
    }


    if ((methodReturnType.isPrimitive || methodReturnType.getName.equals("java.lang.String"))
      && !methodReturnType.getName.equalsIgnoreCase("void")
      || Class.forName("java.lang.Iterable").isAssignableFrom(methodReturnType)) {
      val methodParams = null
      //      val methodParams = for (c <- method.getParameterTypes) yield new Parameter(new ClassOrInterfaceType(c.getName), new VariableDeclaratorId("dontcare"))
      //      val methodExpr: MethodCallExpr = new MethodCallExpr(scope, method.getName, methodParams)
      var i = 0;
      val parameters: java.util.List[Parameter] = (for (p <- method.getParameterTypes.toSeq) yield new Parameter(new ClassOrInterfaceType(p.getName), new VariableDeclaratorId(({
        i += 1;
        i.toString
      })))).asJava
      val parameterAnnotations: Array[Array[Annotation]] = method.getParameterAnnotations

      val methodNode = new InnerMethod(
        scope, new MethodDeclaration(
          0,
          new ClassOrInterfaceType(
            method.getReturnType.toString),
          method.getName,
          parameters),
        parameterAnnotations = Some(parameterAnnotations))

      if (Class.forName("java.lang.Iterable").isAssignableFrom(methodReturnType)) {
        callables :+= new CallableNode(methodNode, refType = new ClassOrInterfaceType(method.getGenericReturnType.toString))

      } else {
        callables :+= new CallableNode(methodNode, refType = new PrimitiveType(PrimitiveType.Primitive.valueOf(methodReturnType.getName.capitalize)))
      }
      //    } else if (Class.forName("java.lang.Iterable").isAssignableFrom(methodReturnType)) {
      //      callables :+= new CallableNode(new MethodCallExpr(scope, method.getName), refType = new ClassOrInterfaceType(method.getGenericReturnType.toString))
    } else if (methodReturnType.isEnum) {
      // todo
    } else {
      // add the object itself:
      //      callables = callables :+ new CallableNode(scope)
      val i = 1

      // todo recursively scan:
      genericReturnType match {
        case t: ParameterizedTypeImpl => None // todo something
        case et: GenericArrayTypeImpl => None
        //          callables ++= extractCallables(t, new MethodCallExpr(scope, method.getName))
        case _ => callables ++= extractCallables(methodReturnType, new MethodCallExpr(scope, method.getName))
      }

    }

    callables
  }

  //  def extractCallables(returnType: ParameterizedTypeImpl, scope: Expression): Seq[CallableNode] = {
  //    // todo
  //    Seq[CallableNode]()
  //  }

  //  def extractCallables(returnType: Class[_], scope: => Expression): Seq[CallableNode] = {
  def extractCallables(returnType: Class[_], scope: => Expression): Seq[CallableNode] = {
    // if returnType is primitive or iterable, return a callable node for it.
    if (returnType.isPrimitive
    //      || Class.forName("java.lang.Iterable").isAssignableFrom(returnType.getClass)
    ) {
      // todo note this might break the world:
      return Seq[CallableNode](new CallableNode(scope, new ClassOrInterfaceType(returnType.toString)))
    } else {
      // else add it and recursively search its members
      var callables: Seq[CallableNode] = Seq[CallableNode]()

      //add it:
      callables :+= new CallableNode(scope, new ClassOrInterfaceType(returnType.getName))

      // recursively search its members:
      for (method <- returnType.getDeclaredMethods) {
        if (!method.getReturnType.toString.equals(returnType.toString)
          && !Modifier.isStatic(method.getModifiers)
          && !method.getReturnType.getName.equals("java.lang.String")) {
          // todo we should allow some recursion
          callables ++= extractCallables(method, scope)
        }
      }

      for (field: Field <- returnType.getDeclaredFields) {
        if (Modifier.isPublic(field.getModifiers) && !Modifier.isStatic(field.getModifiers))
          callables ++= extractCallables(field.getType, new FieldAccessExpr(scope, field.getName))
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
