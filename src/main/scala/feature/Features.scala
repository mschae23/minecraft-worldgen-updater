package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import definition._

object Features {
    val NO_OP = register("no_op", new Feature(Codec[DefaultFeatureConfig]))
    val SIMPLE_BLOCK = register("simple_block", SimpleBlockFeature)
    val TREE = register("tree", TreeFeature)
    val ORE = register("ore", OreFeature)
    val NO_SURFACE_ORE = register("no_surface_ore", NoSurfaceOreFeature)
    val RANDOM_PATCH = register("random_patch", RandomPatchFeature)
    val GROWING_PLANT = register("growing_plant", GrowingPlantFeature)
    val GLOW_LICHEN = register("glow_lichen", new Feature(Codec[GlowLichenFeatureConfig]))
    val BLOCK_COLUMN = register("block_column", new Feature(Codec[BlockColumnFeatureConfig]))
    val VEGETATION_PATCH = register("vegetation_patch", VegetationPatchFeature)
    val WATERLOGGED_VEGETATION_PATCH = register("waterlogged_vegetation_patch", WaterloggedVegetationPatchFeature)
    val RANDOM_SELECTOR = register("random_selector", RandomSelectorFeature)
    val SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", SimpleRandomSelectorFeature)
    val RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", RandomBooleanSelectorFeature)
    val DECORATED = register("decorated", DecoratedFeature)
    val ARRAY_DECORATED = registerCustom("array_decorated", ArrayDecoratedFeature)

    private def register[FC <: FeatureConfig](name: Identifier, feature: Feature[FC]): Feature[FC] = {
        feature.register(name)
        feature
    }

    private def register[FC <: FeatureConfig](name: String, feature: Feature[FC]): Feature[FC] =
        register(Identifier("minecraft", name), feature)

    private def registerCustom[FC <: FeatureConfig](name: String, feature: Feature[FC]): Feature[FC] =
        register(Identifier(UpdaterMain.NAMESPACE, name), feature)
}
