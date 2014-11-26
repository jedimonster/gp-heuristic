package evolution_impl.gpprograms.util

import java.lang.reflect.Type

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

/**
 * Created by itayaza on 26/11/2014.
 */
object ClassUtil {
  def implements[T](clazz: AnyRef, interface: Class[_]): Boolean = {
    clazz match {
      case t: ParameterizedTypeImpl => {
        implements(t.getRawType, interface)
      }
      case c: Class[_] => {
        if (c eq interface)
          return true
        for (i: Type <- c.getGenericInterfaces) {
          if (implements(i, interface))
            return true
        }
      }
    }
    false
  }
}
