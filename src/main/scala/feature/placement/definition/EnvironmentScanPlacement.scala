package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import util.VerticalDirection
import valueprovider.BlockPredicate
import cats.data.Writer

case class EnvironmentScanPlacement(directionOfSearch: VerticalDirection,
                                    targetCondition: BlockPredicate, allowedSearchCondition: BlockPredicate,
                                    maxSteps: Int) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.ENVIRONMENT_SCAN

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        if (context.onlyUpdate) super.process(feature)
        else
            Writer.value(PlacedFeature(feature.feature, EnvironmentScanPlacement(this.directionOfSearch,
                this.targetCondition.process, this.allowedSearchCondition.process, this.maxSteps) :: feature.modifiers))
}
