package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import feature.FeatureConfig
import util.{ BlockState, DataPool, Direction }
import valueprovider.{ BlockStateProvider, IntProvider }

case class GlowLichenFeatureConfig(searchRange: Int, canPlaceOnFloor: Boolean, canPlaceOnCeiling: Boolean, canPlaceOnWall: Boolean,
                                   chanceOfSpreading: Float, canBePlacedOn: List[Identifier]) extends FeatureConfig

object GlowLichenFeatureConfig {
    val currentCodec = Codec.record {
        val searchRange = Codec[Int].orElse(10).fieldOf("search_range").forGetter[GlowLichenFeatureConfig](_.searchRange)
        val canPlaceOnFloor = Codec[Boolean].orElse(false).fieldOf("can_place_on_floor").forGetter[GlowLichenFeatureConfig](_.canPlaceOnFloor)
        val canPlaceOnCeiling = Codec[Boolean].orElse(false).fieldOf("can_place_on_ceiling").forGetter[GlowLichenFeatureConfig](_.canPlaceOnCeiling)
        val canPlaceOnWall = Codec[Boolean].orElse(false).fieldOf("can_place_on_wall").forGetter[GlowLichenFeatureConfig](_.canPlaceOnWall)
        val chanceOfSpreading = Codec[Float].orElse(0.5f).fieldOf("chance_of_spreading").forGetter[GlowLichenFeatureConfig](_.chanceOfSpreading)
        val canBePlacedOn = Codec[List[Identifier]].fieldOf("can_be_placed_on").forGetter[GlowLichenFeatureConfig](_.canBePlacedOn)

        Codec.build(GlowLichenFeatureConfig(searchRange.get, canPlaceOnFloor.get, canPlaceOnCeiling.get, canPlaceOnWall.get, chanceOfSpreading.get, canBePlacedOn.get))
    }

    private val old1Codec = Codec.record {
        val searchRange = Codec[Int].orElse(10).fieldOf("search_range").forGetter[GlowLichenFeatureConfig](_.searchRange)
        val canPlaceOnFloor = Codec[Boolean].orElse(false).fieldOf("can_place_on_floor").forGetter[GlowLichenFeatureConfig](_.canPlaceOnFloor)
        val canPlaceOnCeiling = Codec[Boolean].orElse(false).fieldOf("can_place_on_ceiling").forGetter[GlowLichenFeatureConfig](_.canPlaceOnCeiling)
        val canPlaceOnWall = Codec[Boolean].orElse(false).fieldOf("can_place_on_wall").forGetter[GlowLichenFeatureConfig](_.canPlaceOnWall)
        val chanceOfSpreading = Codec[Float].orElse(0.5f).fieldOf("chance_of_spreading").forGetter[GlowLichenFeatureConfig](_.chanceOfSpreading)
        val canBePlacedOn = Codec[List[BlockState]].fieldOf("can_be_placed_on").forGetter[GlowLichenFeatureConfig](_.canBePlacedOn
            .map(BlockState(_, Map.empty)))

        Codec.build(GlowLichenFeatureConfig(searchRange.get, canPlaceOnFloor.get, canPlaceOnCeiling.get, canPlaceOnWall.get, chanceOfSpreading.get,
            canBePlacedOn.get.map(_.name)))
    }

    given Codec[GlowLichenFeatureConfig] = Codec.alternatives(List(currentCodec, old1Codec))
}
