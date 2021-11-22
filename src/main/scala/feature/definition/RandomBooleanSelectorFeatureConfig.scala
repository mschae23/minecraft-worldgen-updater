package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import feature.placement.PlacedFeatureReference

case class RandomBooleanSelectorFeatureConfig(featureTrue: PlacedFeatureReference,
                                              featureFalse: PlacedFeatureReference) extends FeatureConfig derives Codec
