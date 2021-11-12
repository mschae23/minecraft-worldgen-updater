package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode }
import decorator.{ ConfiguredDecorator, Decorator, DecoratorConfig, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.CountPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.{ DataPool, Weight, Weighted }
import valueprovider.{ ConstantIntProvider, WeightedListIntProvider }
import cats.data.Writer

case class CountExtraDecoratorConfig(count: Int, extraChance: Float, extraCount: Int) extends DecoratorConfig derives Codec

case object CountExtraDecorator extends Decorator(Codec[CountExtraDecoratorConfig]) {
    override def process(config: CountExtraDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult = {
        val weight = getWeight(config.extraChance)
        val defaultWeight = getWeight(1 - config.extraChance)

        PlacedFeature(feature.feature, CountPlacement(WeightedListIntProvider(DataPool(List(
            Weighted.Present(ConstantIntProvider(config.count), Weight(defaultWeight)),
            Weighted.Present(ConstantIntProvider(config.count + config.extraCount), Weight(weight)))))) :: feature.modifiers)
            .process(using context)
    }

    def getWeight(chance: Float): Int = {
        var weight: Float = chance

        while (weight != weight.asInstanceOf[Int]) {
            weight *= 10
        }

        weight.asInstanceOf[Int]
    }
}
