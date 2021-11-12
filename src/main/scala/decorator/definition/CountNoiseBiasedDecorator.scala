package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.{ CountPlacement, NoiseBasedCountPlacement }
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.ConstantIntProvider
import cats.data.Writer

case object CountNoiseBiasedDecorator extends Decorator(Codec[CountNoiseBiasedDecoratorConfig]) {
    override def process(config: CountNoiseBiasedDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        PlacedFeature(feature.feature, NoiseBasedCountPlacement(config.noiseToCountRatio, config.noiseFactor, config.noiseOffset)
            :: feature.modifiers).process(using context)
}
