package de.martenschaefer.minecraft.worldgenupdater
package feature.placement

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ElementError, ElementNode, ValidationError }
import de.martenschaefer.data.util.DataResult.*
import feature.placement.PlacedFeature.ModifierWarnings
import feature.placement.definition.{ BiomePlacement, SquarePlacement }
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }

case class PlacedFeature(feature: ConfiguredFeature[_, _], modifiers: List[PlacementModifier]) {
    def process(using context: FeatureUpdateContext): FeatureProcessResult = {
        @tailrec
        def loop(decorators: List[PlacementModifier], writer: FeatureProcessResult, index: Int): FeatureProcessResult =
            decorators match {
                case head :: tail =>
                    loop(tail, writer.mapBoth((featureWarnings, feature) => head.process(feature)
                        .mapWritten(warnings2 => featureWarnings ::: warnings2
                            .map(_.withPrependedPath(ElementNode.Index(index)).withPrependedPath("placement"))).run),
                        index - 1)
                case Nil => writer
            }

        loop(this.modifiers.reverse, this.feature.feature.process(this.feature.config, context)
            .mapWritten(_.map(_.withPrependedPath("feature"))), this.modifiers.size - 1)
    }

    def getPostProcessWarnings(using context: FeatureUpdateContext): List[ElementError] = {
        @tailrec
        def loop(warnings: ModifierWarnings, modifiers: List[PlacementModifier]): ModifierWarnings = modifiers match {
            case head :: tail => head match {
                case SquarePlacement => // warn for multiple `in_square` modifiers
                    loop(warnings.withHorizontalModifier(head), tail)
                case BiomePlacement => // warn if `biome` modifier is not present (DISABLED)
                    loop(warnings.withBiomeModifier, tail)
                case _ => loop(warnings, tail)
            }
            case _ => warnings
        }

        this.feature.feature.getPostProcessWarnings(this.feature.config, context)
            ::: loop(ModifierWarnings.empty, this.modifiers).toList
    }
}

object PlacedFeature {
    given Codec[PlacedFeature] = Codec.record[PlacedFeature] {
        val feature = Codec[ConfiguredFeature[_, _]].fieldOf("feature").forGetter[PlacedFeature](_.feature)
        val modifiers = Codec[List[PlacementModifier]].fieldOf("placement").forGetter[PlacedFeature](_.modifiers)

        Codec.build(PlacedFeature(feature.get, modifiers.get))
    }

    private case class ModifierWarnings(horizontalModifiers: List[PlacementModifier], hasBiomeModifier: Boolean) {
        def withHorizontalModifier(modifier: PlacementModifier) =
            ModifierWarnings(modifier :: this.horizontalModifiers, this.hasBiomeModifier)

        def withBiomeModifier =
            ModifierWarnings(this.horizontalModifiers, hasBiomeModifier = true)

        def toList: List[ElementError] = {
            if (this.horizontalModifiers.size > 1)
                ValidationError(_ =>
                    "Multiple horizontally spreading modifiers detected: " + this.horizontalModifiers
                        .mkString("", ", ", ""), List.empty) :: ModifierWarnings(List.empty, this.hasBiomeModifier).toList
            /* else if (!this.hasBiomeModifier) // DISABLED
                ValidationError(_ =>
                    "Placed feature is missing `biome` modifier", List.empty) :: ModifierWarnings(this.horizontalModifiers, hasBiomeModifier = true).toList */
            else List.empty
        }
    }

    private object ModifierWarnings {
        def empty: ModifierWarnings = ModifierWarnings(List.empty, hasBiomeModifier = false)
    }
}
