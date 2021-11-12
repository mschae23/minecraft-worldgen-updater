package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import feature.placement.{ PlacementModifier, PlacementModifierType, PlacementModifierTypes }

case class RarityFilterPlacement(chance: Int) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.RARITY_FILTER
}
