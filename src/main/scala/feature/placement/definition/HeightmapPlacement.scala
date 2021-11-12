package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.*
import decorator.{ DecoratorConfig, DefaultDecoratorConfig }
import feature.FeatureProcessResult
import feature.placement.definition.HeightRangePlacement.processHeightReplacingModifier
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import util.HeightmapType
import cats.data.Writer

case class HeightmapPlacement(heightmap: HeightmapType) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.HEIGHTMAP

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        if (context.onlyUpdate) super.process(feature)
        else processHeightReplacingModifier(feature,
            Writer.value(PlacedFeature(feature.feature, HeightmapPlacement(this.heightmap) :: feature.modifiers)))
}
