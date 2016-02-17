import java.io.File

import com.google.common.base.Stopwatch
import bgu.cs.evolution_engine.EvolutionRun
import bgu.cs.evolution_engine.evolution.{ParentSelectionEvolutionStrategy, EvolutionParameters}
import bgu.cs.evolution_engine.selection.TournamentSelection
import evolution_impl.crossover.JavaCodeCrossover
import evolution_impl.fitness.twok.{TwoKFitnessCalculator, BoardWrapper}
import evolution_impl.gpprograms.base.{WildRandomGrowInitializer, JavaCodeIndividual}
import evolution_impl.mutators._

/**
  * Created by Itay on 20-Jan-16.
  */
object TwoKDriver {
  def main(args: Array[String]): Unit = {
    val mutators = List(new ConstantsMutator(0.1),
      new ForLoopsMutator(0.15),
      new RegrowMethodMutator(0.05),
      new DropMethodMutator(0.05),
      new GrowNewwMethodMutator(0.05)
      //    ,    new AlphasMutator(0.2)
    )
    val populationInitializer: WildRandomGrowInitializer = new WildRandomGrowInitializer(params = List(new BoardWrapper(null, 0)), methodCount = 2, new File("individuals/TwoKInd.java"))
    val params = new EvolutionParameters[JavaCodeIndividual](
      fitnessCalculator = new TwoKFitnessCalculator[JavaCodeIndividual](),
      selectionStrategy = new TournamentSelection[JavaCodeIndividual](false),
      crossover = new JavaCodeCrossover(0.25),
      mutators = mutators,
      populationInitializer = populationInitializer,
      generations = 200,
      populationSize = 32)
    val run = new EvolutionRun[JavaCodeIndividual]()
    run.run(params, new ParentSelectionEvolutionStrategy[JavaCodeIndividual](params))
  }
}
