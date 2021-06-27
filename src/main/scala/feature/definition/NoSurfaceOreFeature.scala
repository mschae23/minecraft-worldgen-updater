package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import cats.data.Writer
import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }

case object NoSurfaceOreFeature extends Feature(OreFeatureConfig.old1Codec) {
    override def process(config: OreFeatureConfig): FeatureProcessResult =
        Writer(List.empty, ConfiguredFeature(Features.ORE, OreFeatureConfig(config.targets, config.size, 1f)))
}
