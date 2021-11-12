package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import feature.placement.{ PlacementModifier, PlacementModifierType, PlacementModifierTypes }

case object SquarePlacement extends PlacementModifier {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.IN_SQUARE

    given Codec[SquarePlacement.type] = Codec.unit(SquarePlacement)
}
