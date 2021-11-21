package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import de.martenschaefer.data.util.Identifier
import decorator.{ ConfiguredDecorator, Decorator, DecoratorConfig, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.{ CarvingMaskPlacement, EnvironmentScanPlacement }
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.{ BlockPos, CarverGenerationStep, Direction, VerticalSurfaceType }
import valueprovider.{ MatchingBlocksBlockPredicate, SolidBlockPredicate, TrueBlockPredicate }
import cats.catsInstancesForId
import cats.data.Writer

case class CarvingMaskDecoratorConfig(step: CarverGenerationStep) extends DecoratorConfig derives Codec

case object CarvingMaskDecorator extends Decorator(Codec[CarvingMaskDecoratorConfig]) {
    override def process(config: CarvingMaskDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        CarvingMaskPlacement(config.step).process(feature)(using context)
}
