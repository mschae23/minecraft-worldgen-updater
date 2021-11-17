package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import decorator.{ ConfiguredDecorator, Decorators }
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import util.*

case object DecoratedFeature extends Feature(Codec[DecoratedFeatureConfig]) {
    override def process(config: DecoratedFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult =
        config.feature.feature.process(config.feature.config, context).mapBoth((featureWarnings, feature) =>
            config.decorator.decorator.process(config.decorator.config, feature, context)
                .mapWritten(_.map(_.withPrependedPath("decorator")) ::: featureWarnings.map(
                    _.withPrependedPath("feature"))).run).mapWritten(_.map(_.withPrependedPath("config")))
}
