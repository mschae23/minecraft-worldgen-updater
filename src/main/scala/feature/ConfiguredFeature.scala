package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import de.martenschaefer.minecraft.worldgenupdater.feature.definition.DecoratedFeatureConfig

case class ConfiguredFeature[+FC <: FeatureConfig, +F <: Feature[FC]](feature: F, config: FC)

object ConfiguredFeature {
    // given Registry[ConfiguredFeature[_, _]] = new SimpleRegistry(Identifier("minecraft", "configured_feature"))

    given Codec[ConfiguredFeature[_, _]] = DefaultFeatureCodec(Registry[Feature[_]].dispatch(_.feature, _.codec))

    // initialization of Features done in PlacedFeature object
}
