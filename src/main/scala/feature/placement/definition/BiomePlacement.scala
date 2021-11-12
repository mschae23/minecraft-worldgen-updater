package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import feature.placement.{ PlacementModifier, PlacementModifierType, PlacementModifierTypes }

case object BiomePlacement extends PlacementModifier {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.BIOME

    given Codec[BiomePlacement.type] = Codec.unit(BiomePlacement)
}
