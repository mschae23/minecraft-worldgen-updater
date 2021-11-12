package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.definition.HeightRangePlacement.processHeightReplacingModifier
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import util.YOffset
import valueprovider.{ HeightProvider, UniformHeightProvider }
import cats.data.Writer

case class HeightRangePlacement(height: HeightProvider) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.HEIGHT_RANGE

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        if (context.onlyUpdate) super.process(feature)
        else processHeightReplacingModifier(feature,
            Writer.value(PlacedFeature(feature.feature, HeightRangePlacement(this.height.process) :: feature.modifiers)))
}

object HeightRangePlacement {
    def processHeightReplacingModifier(feature: PlacedFeature, otherwise: => FeatureProcessResult)(using context: FeatureUpdateContext): FeatureProcessResult = {
        @tailrec
        def loop(modifiers: List[PlacementModifier]): Boolean = modifiers match {
            case head :: tail =>
                head match {
                    case HeightRangePlacement(_) => true
                    case HeightmapPlacement(_) => true
                    case _ => loop(tail)
                }
            case _ => false
        }

        if (!context.onlyUpdate && loop(feature.modifiers))
            PlacedFeature(feature.feature, feature.modifiers).process(using context) // don't add this decorator if there is another one later on
        else
            otherwise
    }
}
