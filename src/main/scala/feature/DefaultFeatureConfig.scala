package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.serialization.Codec

case class DefaultFeatureConfig() extends FeatureConfig

object DefaultFeatureConfig {
    val INSTANCE = DefaultFeatureConfig()

    given Codec[DefaultFeatureConfig] = Codec.unit(() => INSTANCE)
}
