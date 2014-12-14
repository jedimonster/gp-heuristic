package japa.parser.ast.expr

/**
 * Created by itayaza on 14/12/2014.
 */

import japa.parser.ast.`type`.Type
import japa.parser.ast.body.{Parameter, MethodDeclaration}

/**
 * represents a method declaration that is in fact accessible via some parameter.
 * it is identical to MethodDeclaration expression except for having a scope
 * it is in fact never used inside an AST, only inside aa CallableNode
 **/
class InnerMethod(val scope: Expression, val methodDeclaration : MethodDeclaration) {

}