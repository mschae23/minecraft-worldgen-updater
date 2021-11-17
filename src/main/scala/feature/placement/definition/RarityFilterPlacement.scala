package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import cats.data.Writer

case class RarityFilterPlacement(chance: Int) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.RARITY_FILTER

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult = this.chance match {
            case 1 if !context.onlyUpdate => Writer.value(feature) // don't add this modifier if chance == 1
            case 0 => super.process(feature).mapBoth((warnings, feature) => (ValidationError(path =>
                    s"$path: Chance is zero; Minecraft will probably crash", List.empty) :: warnings, feature))

            case _ => super.process(feature).map(RarityFilterPlacement.mergeRarityFilterModifiers)
        }
}

object RarityFilterPlacement {
    def mergeRarityFilterModifiers(feature: PlacedFeature)(using context: FeatureUpdateContext): PlacedFeature = {
        @tailrec
        def loop(modifiers: List[PlacementModifier], previous: Option[(PlacementModifier, Int)]): List[PlacementModifier] = modifiers match {
            case head :: tail => head match {
                case RarityFilterPlacement(chance) => previous match {
                    case Some((_, previousChance)) => RarityFilterPlacement(previousChance * chance) :: tail

                    case None => loop(tail, Some(head, chance))
                }

                case _ => previous match {
                    case None => modifiers
                    case Some((modifier, _)) => modifier :: tail
                }
            }

            case Nil => previous match {
                case None => modifiers
                case Some((modifier, _)) => modifier :: Nil
            }
        }

        if (context.onlyUpdate)
            feature
        else
            PlacedFeature(feature.feature, loop(feature.modifiers, None))
    }
}
