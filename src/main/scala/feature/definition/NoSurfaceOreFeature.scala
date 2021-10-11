package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }

case object NoSurfaceOreFeature extends Feature(OreFeatureConfig.old1Codec) {
    override def process(config: OreFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult =
        Features.ORE.process(OreFeatureConfig(config.targets, config.size, 1f), context)
}
