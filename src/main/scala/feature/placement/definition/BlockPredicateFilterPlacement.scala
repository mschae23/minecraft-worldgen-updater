package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.{ Codec, ElementError, ElementNode, ValidationError }
import feature.placement.definition.BlockPredicateFilterPlacement.mergeBlockFilter
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import feature.{ FeatureProcessResult, Features }
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, NotBlockPredicate, TrueBlockPredicate }
import cats.data.Writer

case class BlockPredicateFilterPlacement(predicate: BlockPredicate) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.BLOCK_PREDICATE_FILTER

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult = {
        if (context.onlyUpdate)
            return super.process(feature)

        this.predicate.process match {
            case TrueBlockPredicate => Writer.value(feature)
            case NotBlockPredicate(TrueBlockPredicate) => mergeBlockFilter(feature, NotBlockPredicate(TrueBlockPredicate))
                .mapWritten(ValidationError(path => s"$path: Block filter uses not(true) predicate; "
                    + "the decorated feature will never generate", List(ElementNode.Name("predicate"))) :: _)

            case predicate => mergeBlockFilter(feature, predicate)
        }
    }
}

object BlockPredicateFilterPlacement {
    def mergeBlockFilter(feature: PlacedFeature, predicate: BlockPredicate): FeatureProcessResult = feature match {
        case PlacedFeature(configuredFeature, BlockPredicateFilterPlacement(predicate2) :: modifiers) =>
            Writer.value(PlacedFeature(configuredFeature, BlockPredicateFilterPlacement(AllOfBlockPredicate(List(predicate2, predicate)).process)
                :: modifiers))
        case _ => Writer.value(PlacedFeature(feature.feature, BlockPredicateFilterPlacement(predicate) :: feature.modifiers))
    }
}
