package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import de.martenschaefer.data.util.Identifier
import decorator.{ ConfiguredDecorator, Decorator, DecoratorConfig, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.EnvironmentScanPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.{ BlockPos, Direction, VerticalSurfaceType }
import valueprovider.{ MatchingBlocksBlockPredicate, SolidBlockPredicate, TrueBlockPredicate }
import cats.catsInstancesForId
import cats.data.Writer

case class CaveSurfaceDecoratorConfig(surface: VerticalSurfaceType, floorToCeilingSearchRange: Int) extends DecoratorConfig derives Codec

case object CaveSurfaceDecorator extends Decorator(Codec[CaveSurfaceDecoratorConfig]) {
    override def process(config: CaveSurfaceDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        PlacedFeature(feature.feature, EnvironmentScanPlacement(config.surface match {
            case VerticalSurfaceType.Floor => Direction.Down
            case VerticalSurfaceType.Ceiling => Direction.Up
        }, MatchingBlocksBlockPredicate(List(Identifier("minecraft", "air")), BlockPos.ORIGIN), SolidBlockPredicate(BlockPos.ORIGIN),
            config.floorToCeilingSearchRange) :: feature.modifiers).process(using context)
}
