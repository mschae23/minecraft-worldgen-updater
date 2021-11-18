package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import decorator.{ ConfiguredDecorator, Decorators }
import feature.placement.PlacedFeature
import feature.placement.definition.BiomePlacement
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import util.*

case object DecoratedFeature extends Feature(Codec[DecoratedFeatureConfig]) {
    override def process(config: DecoratedFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        val result: FeatureProcessResult = config.feature.feature.process(config.feature.config, context)
            .mapBoth((featureWarnings, feature) =>
                config.decorator.decorator.process(config.decorator.config, feature, context)
                    .mapWritten(_.map(_.withPrependedPath("decorator")) ::: featureWarnings.map(
                        _.withPrependedPath("feature"))).run).mapWritten(_.map(_.withPrependedPath("config")))

        config.feature match {
            case ConfiguredFeature(Features.DECORATED, DecoratedFeatureConfig(_, _)) => result

            case _ => result.map(feature => PlacedFeature(feature.feature, feature.modifiers.appended(BiomePlacement)))
        }
    }
}
