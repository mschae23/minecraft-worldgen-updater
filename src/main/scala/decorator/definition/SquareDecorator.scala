package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import decorator.{ Decorator, DefaultDecoratorConfig }
import feature.placement.PlacedFeature
import feature.placement.definition.SquarePlacement
import feature.{ ConfiguredFeature, FeatureProcessResult }
import cats.data.Writer

case object SquareDecorator extends Decorator(Codec[DefaultDecoratorConfig]) {
    override def process(config: DefaultDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        PlacedFeature(feature.feature, SquarePlacement :: feature.modifiers).process(using context)
}

case object IcebergDecorator extends Decorator(Codec[DefaultDecoratorConfig]) {
    override def process(config: DefaultDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        PlacedFeature(feature.feature, SquarePlacement :: feature.modifiers).process(using context)
            .mapWritten(ValidationError(path => s"$path: `iceberg` decorator was removed in 1.18-pre1 refactor; replaced with `in_square`", List.empty) :: _)
}
