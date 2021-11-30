package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import util.VerticalDirection
import valueprovider.{ BlockPredicate, TrueBlockPredicate }
import cats.data.Writer

case class EnvironmentScanPlacement(directionOfSearch: VerticalDirection,
                                    targetCondition: BlockPredicate, allowedSearchCondition: BlockPredicate,
                                    maxSteps: Int) extends PlacementModifier {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.ENVIRONMENT_SCAN

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        if (context.onlyUpdate) super.process(feature)
        else
            Writer.value(PlacedFeature(feature.feature, EnvironmentScanPlacement(this.directionOfSearch,
                this.targetCondition.process, this.allowedSearchCondition.process, this.maxSteps) :: feature.modifiers))
}

object EnvironmentScanPlacement {
    given Codec[EnvironmentScanPlacement] = Codec.record {
        val directionOfSearch = Codec[VerticalDirection].fieldOf("direction_of_search").forGetter[EnvironmentScanPlacement](_.directionOfSearch)
        val targetCondition = Codec[BlockPredicate].fieldOf("target_condition").forGetter[EnvironmentScanPlacement](_.targetCondition)
        val allowedSearchCondition = Codec[BlockPredicate].orElse(TrueBlockPredicate).fieldOf("allowed_search_condition").forGetter[EnvironmentScanPlacement](_.allowedSearchCondition)
        val maxSteps = Codec[Int].fieldOf("max_steps").forGetter[EnvironmentScanPlacement](_.maxSteps)

        Codec.build(EnvironmentScanPlacement(directionOfSearch.get, targetCondition.get, allowedSearchCondition.get, maxSteps.get))
    }
}
