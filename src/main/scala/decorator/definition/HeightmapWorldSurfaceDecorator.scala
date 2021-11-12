package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators, DefaultDecoratorConfig }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.HeightmapType
import cats.data.Writer

case object HeightmapWorldSurfaceDecorator extends Decorator(Codec[DefaultDecoratorConfig]) {
    override def process(config: DefaultDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        Decorators.HEIGHTMAP.process(HeightmapDecoratorConfig(HeightmapType.WorldSurfaceWg), feature, context)
}
