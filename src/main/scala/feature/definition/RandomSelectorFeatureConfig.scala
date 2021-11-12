package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.definition.RandomSelectorFeatureConfig.RandomSelectorFeatureEntry
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, FeatureConfig }

case class RandomSelectorFeatureConfig(features: List[RandomSelectorFeatureEntry],
                                       default: PlacedFeature) extends FeatureConfig derives Codec

object RandomSelectorFeatureConfig {
    case class RandomSelectorFeatureEntry(feature: PlacedFeature, chance: Float) derives Codec
}
