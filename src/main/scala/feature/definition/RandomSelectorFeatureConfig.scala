package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, FeatureConfig }
import de.martenschaefer.minecraft.worldgenupdater.feature.definition.RandomSelectorFeatureConfig.RandomSelectorFeatureEntry

case class RandomSelectorFeatureConfig(val features: List[RandomSelectorFeatureEntry],
                                       val default: ConfiguredFeature[_, _]) extends FeatureConfig derives Codec

object RandomSelectorFeatureConfig {
    case class RandomSelectorFeatureEntry(val feature: ConfiguredFeature[_, _], val chance: Float) derives Codec
}
