package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, FeatureConfig }

case class SimpleRandomSelectorFeatureConfig(val features: List[ConfiguredFeature[_, _]]) extends FeatureConfig derives Codec
