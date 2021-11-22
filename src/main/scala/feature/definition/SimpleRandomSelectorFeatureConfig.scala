package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.placement.PlacedFeatureReference
import feature.{ ConfiguredFeature, FeatureConfig }

case class SimpleRandomSelectorFeatureConfig(features: List[PlacedFeatureReference]) extends FeatureConfig derives Codec
