package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.*
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import valueprovider.{ ConstantIntProvider, IntProvider }
import cats.data.Writer

case class NoiseBasedCountPlacement(noiseToCountRatio: Int,
                                    noiseFactor: Float, noiseOffset: Float) extends PlacementModifier {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.NOISE_BASED_COUNT
}

object NoiseBasedCountPlacement {
    given Codec[NoiseBasedCountPlacement] = Codec.record {
        val noiseToCountRatio = Codec[Int].fieldOf("noise_to_count_ratio").forGetter[NoiseBasedCountPlacement](_.noiseToCountRatio)
        val noiseFactor = Codec[Float].fieldOf("noise_factor").forGetter[NoiseBasedCountPlacement](_.noiseFactor)
        val noiseOffset = Codec[Float].orElse(0f).fieldOf("noise_offset").forGetter[NoiseBasedCountPlacement](_.noiseOffset)

        Codec.build(NoiseBasedCountPlacement(noiseToCountRatio.get, noiseFactor.get, noiseOffset.get))
    }
}
