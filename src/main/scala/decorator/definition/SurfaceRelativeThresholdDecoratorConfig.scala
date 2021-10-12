package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import util.{ BlockState, HeightmapType }

case class SurfaceRelativeThresholdDecoratorConfig(heightmap: HeightmapType,
                                                   minInclusive: Int, maxInclusive: Int) extends DecoratorConfig

object SurfaceRelativeThresholdDecoratorConfig {
    given Codec[SurfaceRelativeThresholdDecoratorConfig] = Codec.record {
        val heightmap = Codec[HeightmapType].fieldOf("heightmap").forGetter[SurfaceRelativeThresholdDecoratorConfig](_.heightmap)
        val min = Codec[Int].orElse(Int.MinValue).fieldOf("min_inclusive").forGetter[SurfaceRelativeThresholdDecoratorConfig](_.minInclusive)
        val max = Codec[Int].orElse(Int.MaxValue).fieldOf("max_inclusive").forGetter[SurfaceRelativeThresholdDecoratorConfig](_.maxInclusive)

        Codec.build(SurfaceRelativeThresholdDecoratorConfig(heightmap.get, min.get, max.get))
    }
}
