package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.{ Decorator, Decorators, DefaultDecoratorConfig }
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, FeatureProcessResult }
import cats.catsInstancesForId

case object HeightmapSpreadDoubleDecorator extends Decorator(Codec[HeightmapDecoratorConfig]) {
    override def process(config: HeightmapDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        Decorators.HEIGHTMAP.process(config, feature, context)
            .flatMap(feature => Decorators.CHANCE.process(ChanceDecoratorConfig(64), feature, context))
}
