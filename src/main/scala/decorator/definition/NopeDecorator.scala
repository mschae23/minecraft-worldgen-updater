package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.{ Decorator, DefaultDecoratorConfig }
import feature.{ ConfiguredFeature, FeatureProcessResult }
import feature.placement.PlacedFeature
import cats.data.Writer

case object NopeDecorator extends Decorator(Codec[DefaultDecoratorConfig]) {
    override def process(config: DefaultDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        Writer.value(feature) // Don't add any decorator
}
