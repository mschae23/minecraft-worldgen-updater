package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators, DefaultDecoratorConfig }
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import feature.definition.DecoratedFeatureConfig
import util.HeightmapType

case object TopSolidHeightmapDecorator extends Decorator(Codec[DefaultDecoratorConfig]) {
    override def process(config: DefaultDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult =
        Features.DECORATED.process(DecoratedFeatureConfig(feature, ConfiguredDecorator(Decorators.HEIGHTMAP,
            HeightmapDecoratorConfig(HeightmapType.OceanFloorWg))), context)
}
