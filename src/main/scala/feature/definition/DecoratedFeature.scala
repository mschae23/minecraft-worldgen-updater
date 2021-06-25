package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, Feature }
import util._

case object DecoratedFeature extends Feature(Codec[DecoratedFeatureConfig]) {
    override def process(config: DecoratedFeatureConfig): FeatureProcessResult =
        config.feature.feature.process(config.feature.config).mapBoth((featureWarnings, feature) =>
            config.decorator.decorator.process(config.decorator.config, feature)
                .mapWritten(_.map(_.withPrependedPath("decorator")) ::: featureWarnings.map(
                    _.withPrependedPath("feature"))).run).mapWritten(_.map(_.withPrependedPath("config")))
}
