package evolution_impl.log

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import evolution_impl.gpprograms.base.JavaCodeIndividual

/**
 * Created by itayaza on 26/10/2014.
 */
object GPEvolutionLogger {
  def saveBadIndividual(individual: JavaCodeIndividual) = {
    Files.write(Paths.get("errors/" + individual.className.toString()), individual.ast.toString.getBytes(StandardCharsets.UTF_8))
  }
}
