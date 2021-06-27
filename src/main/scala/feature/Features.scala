package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import definition._

object Features {
    val NO_OP = register("no_op", new Feature(Codec[DefaultFeatureConfig]))
    val TREE = register("tree", TreeFeature)
    val ORE = register("ore", OreFeature)
    val NO_SURFACE_ORE = register("no_surface_ore", NoSurfaceOreFeature)
    val DECORATED = register("decorated", DecoratedFeature)
    val ARRAY_DECORATED = registerCustom("array_decorated", ArrayDecoratedFeature)

    private def register[FC <: FeatureConfig](name: String, feature: Feature[FC]): Feature[FC] = {
        feature.register(Identifier("minecraft", name))
        feature
    }

    private def registerCustom[FC <: FeatureConfig](name: String, feature: Feature[FC]): Feature[FC] = {
        feature.register(Identifier(UpdaterMain.NAMESPACE, name))
        feature
    }
}
