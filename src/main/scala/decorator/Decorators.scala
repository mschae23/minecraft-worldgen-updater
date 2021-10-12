package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import decorator.definition.*

object Decorators {
    val NOPE = register("nope", new Decorator(Codec[DefaultDecoratorConfig]))
    val DECORATED = register("decorated", DecoratedDecorator)
    val SQUARE = register("square", new Decorator(Codec[DefaultDecoratorConfig]))
    val ICEBERG = register("iceberg", new Decorator(Codec[DefaultDecoratorConfig]))
    val RANGE = register("range", RangeDecorator)
    val HEIGHTMAP = register("heightmap", HeightmapDecorator)
    val TOP_SOLID_HEIGHTMAP = register("top_solid_heightmap", TopSolidHeightmapDecorator)
    val HEIGHTMAP_WORLD_SURFACE = register("heightmap_world_surface", HeightmapWorldSurfaceDecorator)
    val COUNT = register("count", CountDecorator)
    val CHANCE = register("chance", ChanceDecorator)
    val SPREAD_32_ABOVE = register("spread_32_above", Spread32AboveDecorator)
    val HEIGHTMAP_SPREAD_DOUBLE = register("heightmap_spread_double", HeightmapSpreadDoubleDecorator)
    val SURFACE_RELATIVE_THRESHOLD = register("surface_relative_threshold", new Decorator(Codec[SurfaceRelativeThresholdDecoratorConfig]))
    val WATER_DEPTH_THRESHOLD = register("water_depth_threshold", WaterDepthThresholdDecorator)
    val BLOCK_SURVIVES_FILTER = register("block_survives_filter", BlockSurvivesFilterDecorator)
    val BLOCK_FILTER = register("block_filter", BlockFilterDecorator)
    val ENVIRONMENT_SCAN = register("environment_scan", EnvironmentScanDecorator)

    private def register[DC <: DecoratorConfig](name: String, decorator: Decorator[DC]): Decorator[DC] = {
        decorator.register(Identifier("minecraft", name))
        decorator
    }

    private def registerCustom[DC <: DecoratorConfig](name: String, decorator: Decorator[DC]): Decorator[DC] = {
        decorator.register(Identifier(UpdaterMain.NAMESPACE, name))
        decorator
    }
}
