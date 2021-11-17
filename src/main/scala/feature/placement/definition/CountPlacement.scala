package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.*
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import valueprovider.{ ConstantIntProvider, IntProvider }
import cats.data.Writer

case class CountPlacement(count: IntProvider) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.COUNT

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        this.count.process match {
            case ConstantIntProvider(value) if !context.onlyUpdate && value == 1 => Writer.value(feature) // don't add this modifier if count == 1
            case ConstantIntProvider(value) if value == 0 => super.process(feature).mapBoth((warnings, feature) => (ValidationError(
                    path => s"$path: Count is zero; the decorated feature will never generate", List.empty) :: warnings, feature))

            case count => Writer.value(PlacedFeature(feature.feature, CountPlacement(if (context.onlyUpdate) this.count else count)
                :: feature.modifiers))
        }
}
