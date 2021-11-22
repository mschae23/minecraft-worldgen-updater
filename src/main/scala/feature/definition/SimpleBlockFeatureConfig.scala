package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import feature.definition.SimpleBlockFeatureConfig.Old1
import util.BlockState
import valueprovider.BlockStateProvider

case class SimpleBlockFeatureConfig(toPlace: BlockStateProvider,
                                    old1: Option[Old1] = None) extends FeatureConfig

object SimpleBlockFeatureConfig {
    case class Old1(placeOn: List[BlockState], placeIn: List[BlockState], placeUnder: List[BlockState])

    val old1Codec: Codec[SimpleBlockFeatureConfig] = Codec.record {
        val toPlace = Codec[BlockStateProvider].fieldOf("to_place").forGetter[SimpleBlockFeatureConfig](_.toPlace)
        val placeOn = Codec[List[BlockState]].orElse(List.empty).fieldOf("place_on").forGetter[SimpleBlockFeatureConfig](_.old1.get.placeOn)
        val placeIn = Codec[List[BlockState]].orElse(List.empty).fieldOf("place_in").forGetter[SimpleBlockFeatureConfig](_.old1.get.placeIn)
        val placeUnder = Codec[List[BlockState]].orElse(List.empty).fieldOf("place_under").forGetter[SimpleBlockFeatureConfig](_.old1.get.placeUnder)

        Codec.build(SimpleBlockFeatureConfig(toPlace.get, Some(Old1(placeOn.get, placeIn.get, placeUnder.get))))
    }

    val currentCodec: Codec[SimpleBlockFeatureConfig] = Codec.record {
        val toPlace = Codec[BlockStateProvider].fieldOf("to_place").forGetter[SimpleBlockFeatureConfig](_.toPlace)

        Codec.build(SimpleBlockFeatureConfig(toPlace.get))
    }

    given Codec[SimpleBlockFeatureConfig] = Codec.alternatives(List(old1Codec, currentCodec))
}
