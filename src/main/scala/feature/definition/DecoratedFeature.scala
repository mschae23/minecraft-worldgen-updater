package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, Feature }

case object DecoratedFeature extends Feature(Codec[DecoratedFeatureConfig]) {
    override def process(config: DecoratedFeatureConfig): FeatureProcessResult =
        config.decorator.decorator.process(config.decorator.config, config.feature)
            .mapWritten(_.map(_.withPrependedPath("decorator").withPrependedPath("config")))
}
