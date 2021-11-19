package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import feature.placement.PlacedFeature

case class RandomBooleanSelectorFeatureConfig(val featureTrue: PlacedFeature,
                                              val featureFalse: PlacedFeature) extends FeatureConfig derives Codec
