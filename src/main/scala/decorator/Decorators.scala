package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import decorator.definition._

object Decorators {
    val RANGE = register("range", RangeDecorator)
    val HEIGHTMAP = register("heightmap", HeightmapDecorator)
    val COUNT = register("count", CountDecorator)
    val WATER_DEPTH_THRESHOLD = register("water_depth_threshold", WaterDepthThresholdDecorator)

    private def register[DC <: DecoratorConfig](name: String, decorator: Decorator[DC]): Decorator[DC] = {
        decorator.register(Identifier("minecraft", name))
        decorator
    }

    private def registerCustom[DC <: DecoratorConfig](name: String, decorator: Decorator[DC]): Decorator[DC] = {
        decorator.register(Identifier(UpdaterMain.NAMESPACE, name))
        decorator
    }
}
