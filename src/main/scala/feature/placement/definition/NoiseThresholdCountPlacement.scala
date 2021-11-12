package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.*
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import valueprovider.{ ConstantIntProvider, IntProvider }
import cats.data.Writer

case class NoiseThresholdCountPlacement(noiseLevel: Double,
                                        belowNoise: Int, aboveNoise: Int) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.NOISE_THRESHOLD_COUNT
}


