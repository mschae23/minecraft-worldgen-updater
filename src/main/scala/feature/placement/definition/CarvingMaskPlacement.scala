package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.*
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import util.CarverGenerationStep
import valueprovider.{ ConstantIntProvider, IntProvider }
import cats.data.Writer

case class CarvingMaskPlacement(step: CarverGenerationStep) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.CARVING_MASK
}
