package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, DecoratorConfig, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.CountPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.{ DataPool, Weight, Weighted }
import valueprovider.{ ConstantIntProvider, WeightedListIntProvider }
import cats.catsInstancesForId
import cats.data.Writer

case class CountExtraDecoratorConfig(count: Int, extraChance: Float, extraCount: Int) extends DecoratorConfig derives Codec

case object CountExtraDecorator extends Decorator(Codec[CountExtraDecoratorConfig]) {
    override def process(config: CountExtraDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult = {
        val weightResult = getWeight(config.extraChance)

        weightResult.flatMap((weight, extraWeight) => CountPlacement(WeightedListIntProvider(DataPool(List(
            Weighted.Present(ConstantIntProvider(config.count), Weight(weight)),
            Weighted.Present(ConstantIntProvider(config.count + config.extraCount), Weight(extraWeight))))))
            .process(feature)(using context))
    }

    def getWeight(chance: Float): ProcessResult[(Int, Int)] = {
        if (chance < 1.0E-5)
            return Writer.value((1, 0))
        else if (chance > 0.99999)
            return Writer.value((0, 1))

        val weight: Double = 1.0 / chance
        val weightAsInt: Int = weight.asInstanceOf[Int]

        if (Math.abs(weight - weightAsInt.asInstanceOf[Double]) > 1.0E-5) {
            val weightAsIntPlusOne = weightAsInt + 1

            if (Math.abs(weight - weightAsIntPlusOne.asInstanceOf[Double]) > 1.0E-5)
                Writer(List(ValidationError(path => s"$path: `extra_chance` cannot be represented as weight")
                    .withPrependedPath("extra_chance").withPrependedPath("config")), (weightAsInt - 1, 1))
            else
                Writer.value((weightAsIntPlusOne - 1, 1))
        } else
            Writer.value((weightAsInt - 1, 1))
    }
}
