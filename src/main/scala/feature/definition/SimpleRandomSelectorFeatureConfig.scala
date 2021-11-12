package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.placement.PlacedFeature
import feature.{ ConfiguredFeature, FeatureConfig }

case class SimpleRandomSelectorFeatureConfig(features: List[PlacedFeature]) extends FeatureConfig derives Codec
