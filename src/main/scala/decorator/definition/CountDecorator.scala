package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.CountPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.ConstantIntProvider
import cats.data.Writer

case object CountDecorator extends Decorator(Codec[CountDecoratorConfig]) {
    override def process(config: CountDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        CountPlacement(config.count).process(feature)(using context)
}
