package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.{ PlacedFeature, PlacementModifier }
import feature.placement.definition.HeightRangePlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.ConstantIntProvider
import cats.data.Writer

case object RangeDecorator extends Decorator(Codec[RangeDecoratorConfig]) {
    override def process(config: RangeDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        HeightRangePlacement.processHeightReplacingModifier(feature, Writer.value(PlacedFeature(feature.feature,
            HeightRangePlacement(if (context.onlyUpdate) config.height else config.height.process) :: feature.modifiers)))(using context)
}
