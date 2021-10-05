package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.{ ConfiguredFeature, FeatureConfig }

case class RandomBooleanSelectorFeatureConfig(val featureTrue: ConfiguredFeature[_, _],
                                              val featureFalse: ConfiguredFeature[_, _]) extends FeatureConfig derives Codec
