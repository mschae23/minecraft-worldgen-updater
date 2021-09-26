package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.Decorator
import feature.{ ConfiguredFeature, FeatureProcessResult }
import valueprovider.ConstantIntProvider

case object HeightmapDecorator extends Decorator(Codec[HeightmapDecoratorConfig]) {
    override def process(config: HeightmapDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult =
        RangeDecorator.processHeightReplacingDecorator(feature, super.process(config, feature, context), context)
}
