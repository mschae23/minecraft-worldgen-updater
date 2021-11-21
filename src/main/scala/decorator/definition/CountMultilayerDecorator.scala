package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import de.martenschaefer.data.util.Identifier
import decorator.{ ConfiguredDecorator, Decorator, DecoratorConfig, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.{ CarvingMaskPlacement, CountMultilayerPlacement, EnvironmentScanPlacement }
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.{ BlockPos, CarverGenerationStep, Direction, VerticalSurfaceType }
import valueprovider.{ IntProvider, MatchingBlocksBlockPredicate, SolidBlockPredicate, TrueBlockPredicate }
import cats.catsInstancesForId
import cats.data.Writer

case class CountMultilayerDecoratorConfig(count: IntProvider) extends DecoratorConfig derives Codec

case object CountMultilayerDecorator extends Decorator(Codec[CountMultilayerDecoratorConfig]) {
    override def process(config: CountMultilayerDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        CountMultilayerPlacement(config.count).process(feature)(using context)
}
