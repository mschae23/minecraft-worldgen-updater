package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import cats.catsInstancesForId
import cats.data.Writer

case object DecoratedDecorator extends Decorator(Codec[DecoratedDecoratorConfig]) {
    override def process(config: DecoratedDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult = {
        for {
            processedInner <- config.inner.decorator.process(config.inner.config, feature, context)
                .mapWritten(_.map(_.withPrependedPath("inner")))
            processedOuter: PlacedFeature <- config.outer.decorator.process(config.outer.config, processedInner, context)
                .mapWritten(_.map(_.withPrependedPath("outer")))
        } yield processedOuter
    }
}
