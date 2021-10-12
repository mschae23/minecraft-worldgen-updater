package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import cats.catsInstancesForId
import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }

case object DecoratedDecorator extends Decorator(Codec[DecoratedDecoratorConfig]) {
    override def process(config: DecoratedDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult = {
        val processed = config.inner.decorator.process(config.inner.config, feature, context)
            .mapWritten(_.map(_.withPrependedPath("inner"))).flatMap(inner =>
            config.outer.decorator.process(config.outer.config, inner, context)
                .mapWritten(_.map(_.withPrependedPath("outer"))))
            .mapWritten(_.map(_.withPrependedPath("config")))

        processed.run._2 match {
            case ConfiguredFeature(Features.DECORATED, DecoratedFeatureConfig(
            ConfiguredFeature(Features.DECORATED, DecoratedFeatureConfig(processedFeature,
            ConfiguredDecorator(config.inner.decorator, innerConfig))),
            ConfiguredDecorator(config.outer.decorator, outerConfig))) =>
                processed.map(_ => Features.DECORATED.configure(DecoratedFeatureConfig(
                    processedFeature, Decorators.DECORATED.configure(DecoratedDecoratorConfig(
                        config.outer.decorator.configure(outerConfig),
                        config.inner.decorator.configure(innerConfig))))))

            case _ => processed
        }
    }
}
