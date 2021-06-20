package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import definition.DecoratedFeatureConfig

object Features {
    val NOPE = register("nope", new Feature(Codec[DefaultFeatureConfig]))
    val DECORATED = register("decorated", new Feature(Codec[DecoratedFeatureConfig]))

    private def register[FC <: FeatureConfig](name: String, feature: Feature[FC]): Feature[FC] = {
        feature.register(Identifier("minecraft", name))
        feature
    }

    private def registerCustom[FC <: FeatureConfig](name: String, feature: Feature[FC]): Feature[FC] = {
        feature.register(Identifier(UpdaterMain.NAMESPACE, name))
        feature
    }
}
