package de.martenschaefer.minecraft.worldgenupdater
package feature.placement

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.{ Codec, ElementError }
import de.martenschaefer.data.util.Identifier
import feature.FeatureProcessResult
import cats.data.Writer

trait PlacementModifier {
    def modifierType: PlacementModifierType[_]

    /**
     * Adds the current placement modifier to the passed placed feature.
     *
     * @param feature The [[PlacedFeature]] to add {@code this} placement modifier to.
     * @return the new {@code PlacedFeature}
     */
    def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        Writer.value(PlacedFeature(feature.feature, this :: feature.modifiers))
}

case class PlacementModifierType[M <: PlacementModifier](codec: Codec[M])

object PlacementModifier {
    given Codec[PlacementModifier] = Registry[PlacementModifierType[_]].dispatch(_.modifierType, _.codec)

    PlacementModifierTypes // init
}

object PlacementModifierType {
    given Registry[PlacementModifierType[_]] = new SimpleRegistry(Identifier("minecraft", "placement_modifier_type"))
}

object PlacementModifierTypes {
    import feature.placement.definition.*

    val BLOCK_PREDICATE_FILTER = register("block_predicate_filter", Codec[BlockPredicateFilterPlacement])
    val RARITY_FILTER = register("rarity_filter", Codec[RarityFilterPlacement])
    val SURFACE_RELATIVE_THRESHOLD_FILTER = register("surface_relative_threshold_filter", Codec[SurfaceRelativeThresholdFilterPlacement])
    val SURFACE_WATER_DEPTH_FILTER = register("surface_water_depth_filter", Codec[SurfaceWaterDepthFilterPlacement])
    val BIOME = register("biome", Codec[BiomePlacement.type])
    val COUNT = register("count", Codec[CountPlacement])
    val NOISE_BASED_COUNT = register("noise_based_count", Codec[NoiseBasedCountPlacement])
    val NOISE_THRESHOLD_COUNT = register("noise_threshold_count", Codec[NoiseThresholdCountPlacement])
    val COUNT_ON_EVERY_LAYER = register("count_on_every_layer", Codec[CountMultilayerPlacement])
    val ENVIRONMENT_SCAN = register("environment_scan", Codec[EnvironmentScanPlacement])
    val HEIGHTMAP = register("heightmap", Codec[HeightmapPlacement])
    val HEIGHT_RANGE = register("height_range", Codec[HeightRangePlacement])
    val RANDOM_OFFSET = register("random_offset", Codec[RandomOffsetPlacement])
    val IN_SQUARE = register("in_square", Codec[SquarePlacement.type])
    val CARVING_MASK = register("carving_mask", Codec[CarvingMaskPlacement])

    private def register[M <: PlacementModifier](name: String, codec: Codec[M]): PlacementModifierType[M] = {
        val modifierType = PlacementModifierType(codec)

        modifierType.register(Identifier("minecraft", name))
        modifierType
    }
}
