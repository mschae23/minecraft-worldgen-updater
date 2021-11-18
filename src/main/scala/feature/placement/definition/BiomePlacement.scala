package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import cats.data.Writer

case object BiomePlacement extends PlacementModifier {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.BIOME

    given Codec[BiomePlacement.type] = Codec.unit(BiomePlacement)

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        feature.modifiers match {
            case head :: _ if head == BiomePlacement => Writer.value(feature) // Merge subsequent `biome` modifiers

            case _ => super.process(feature)
        }
}
