package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.*
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import valueprovider.{ ConstantIntProvider, IntProvider }
import cats.data.Writer

case class CountMultilayerPlacement(count: IntProvider) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.COUNT_ON_EVERY_LAYER

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        if (context.onlyUpdate) super.process(feature)
        else Writer.value(PlacedFeature(feature.feature, CountMultilayerPlacement(this.count.process) :: feature.modifiers))
}
