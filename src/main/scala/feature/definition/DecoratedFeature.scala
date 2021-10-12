package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import de.martenschaefer.minecraft.worldgenupdater.decorator.{ ConfiguredDecorator, Decorators }
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import util._

case object DecoratedFeature extends Feature(Codec[DecoratedFeatureConfig]) {
    override def process(config: DecoratedFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult =
        config.feature.feature.process(config.feature.config, context).mapBoth((featureWarnings, feature) =>
            config.decorator.decorator.process(config.decorator.config, feature, context)
                .mapWritten(_.map(_.withPrependedPath("decorator")) ::: featureWarnings.map(
                    _.withPrependedPath("feature"))).run).mapWritten(_.map(_.withPrependedPath("config")))

    override def getPostProcessWarnings(config: DecoratedFeatureConfig, context: FeatureUpdateContext): List[ElementError] = {
        @tailrec
        def loop(warnings: DecoratorWarnings, feature: ConfiguredFeature[_, _]): DecoratorWarnings = feature match {
            case ConfiguredFeature(Features.DECORATED, config: DecoratedFeatureConfig) =>
                config.decorator match {
                    case ConfiguredDecorator(Decorators.SQUARE, _) =>
                        loop(warnings.withHorizontalDecorator(config.decorator), config.feature)
                    case ConfiguredDecorator(Decorators.ICEBERG, _) =>
                        loop(warnings.withHorizontalDecorator(config.decorator), config.feature)
                    case _ => loop(warnings, config.feature)
                }
            case _ => warnings
        }

        loop(DecoratorWarnings(List.empty), ConfiguredFeature(Features.DECORATED, config)).toList
    }

    private case class DecoratorWarnings(val horizontalDecorators: List[ConfiguredDecorator[_, _]]) {
        def withHorizontalDecorator(feature: ConfiguredDecorator[_, _]) =
            DecoratorWarnings(feature :: this.horizontalDecorators)

        def toList: List[ElementError] = {
            if (this.horizontalDecorators.size > 1)
                List(ValidationError(_ =>
                    "Multiple horizontally spreading decorators detected: " + this.horizontalDecorators
                        .mkString("", ", ", ""), List.empty))
            else List.empty
        }
    }
}
