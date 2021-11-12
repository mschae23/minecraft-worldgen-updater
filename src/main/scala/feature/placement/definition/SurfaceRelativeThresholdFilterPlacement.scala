package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import feature.placement.{ PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import util.{ BlockState, HeightmapType }

case class SurfaceRelativeThresholdFilterPlacement(heightmap: HeightmapType,
                                                   minInclusive: Int, maxInclusive: Int) extends PlacementModifier {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.SURFACE_RELATIVE_THRESHOLD_FILTER
}

object SurfaceRelativeThresholdFilterPlacement {
    given Codec[SurfaceRelativeThresholdFilterPlacement] = Codec.record {
        val heightmap = Codec[HeightmapType].fieldOf("heightmap").forGetter[SurfaceRelativeThresholdFilterPlacement](_.heightmap)
        val min = Codec[Int].orElse(Int.MinValue).fieldOf("min_inclusive").forGetter[SurfaceRelativeThresholdFilterPlacement](_.minInclusive)
        val max = Codec[Int].orElse(Int.MaxValue).fieldOf("max_inclusive").forGetter[SurfaceRelativeThresholdFilterPlacement](_.maxInclusive)

        Codec.build(SurfaceRelativeThresholdFilterPlacement(heightmap.get, min.get, max.get))
    }
}
