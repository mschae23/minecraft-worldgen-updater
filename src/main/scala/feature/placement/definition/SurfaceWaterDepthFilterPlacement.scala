package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import feature.placement.{ PlacementModifier, PlacementModifierType, PlacementModifierTypes }

case class SurfaceWaterDepthFilterPlacement(maxWaterDepth: Int) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.SURFACE_WATER_DEPTH_FILTER
}
