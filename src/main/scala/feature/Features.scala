package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import definition._

object Features {
    val NO_OP = register("no_op", new Feature(Codec[DefaultFeatureConfig]))
    val SIMPLE_BLOCK = register("simple_block", new Feature(Codec[SimpleBlockFeatureConfig]))
    val TREE = register("tree", TreeFeature)
    val ORE = register("ore", OreFeature)
    val NO_SURFACE_ORE = register("no_surface_ore", NoSurfaceOreFeature)
    val RANDOM_PATCH = register("random_patch", RandomPatchFeature)
    val GROWING_PLANT = register("growing_plant", GrowingPlantFeature)
    val BLOCK_COLUMN = register("block_column", new Feature(Codec[BlockColumnFeatureConfig]))
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
