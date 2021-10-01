package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import cats.catsInstancesForId
import de.martenschaefer.data.serialization.Codec
import decorator.{ Decorator, Decorators, DefaultDecoratorConfig }
import feature.{ ConfiguredFeature, FeatureProcessResult }

case object HeightmapSpreadDoubleDecorator extends Decorator(Codec[HeightmapDecoratorConfig]) {
    override def process(config: HeightmapDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult =
        Decorators.HEIGHTMAP.process(config, feature, context)
            .flatMap(feature => Decorators.CHANCE.process(ChanceDecoratorConfig(64), feature, context))
}
