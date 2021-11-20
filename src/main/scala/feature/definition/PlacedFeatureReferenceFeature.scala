package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import feature.{ Feature, FeatureConfig }

case object PlacedFeatureReferenceFeature extends Feature(Codec[PlacedFeatureReferenceFeatureConfig]) {
}

case class PlacedFeatureReferenceFeatureConfig(feature: Identifier) extends FeatureConfig

object PlacedFeatureReferenceFeatureConfig {
    given Codec[PlacedFeatureReferenceFeatureConfig] =
        Codec.derived[PlacedFeatureReferenceFeatureConfig].internal
}
