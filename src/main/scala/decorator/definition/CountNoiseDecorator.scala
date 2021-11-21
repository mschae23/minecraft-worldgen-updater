package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import de.martenschaefer.data.util.Identifier
import decorator.{ ConfiguredDecorator, Decorator, DecoratorConfig, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.{ CarvingMaskPlacement, EnvironmentScanPlacement, NoiseThresholdCountPlacement }
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.{ BlockPos, CarverGenerationStep, Direction, VerticalSurfaceType }
import valueprovider.{ MatchingBlocksBlockPredicate, SolidBlockPredicate, TrueBlockPredicate }
import cats.catsInstancesForId
import cats.data.Writer

case class CountNoiseDecoratorConfig(noiseLevel: Double, belowNoise: Int, aboveNoise: Int) extends DecoratorConfig derives Codec

case object CountNoiseDecorator extends Decorator(Codec[CountNoiseDecoratorConfig]) {
    override def process(config: CountNoiseDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        NoiseThresholdCountPlacement(config.noiseLevel, config.belowNoise, config.aboveNoise).process(feature)(using context)
}
