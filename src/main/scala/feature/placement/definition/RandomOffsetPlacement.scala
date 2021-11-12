package de.martenschaefer.minecraft.worldgenupdater
package feature.placement.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import feature.FeatureProcessResult
import feature.placement.{ PlacedFeature, PlacementModifier, PlacementModifierType, PlacementModifierTypes }
import util.YOffset
import valueprovider.{ HeightProvider, IntProvider, UniformHeightProvider }
import cats.data.Writer

case class RandomOffsetPlacement(xzSpread: IntProvider, ySpread: IntProvider) extends PlacementModifier derives Codec {
    override def modifierType: PlacementModifierType[_] = PlacementModifierTypes.RANDOM_OFFSET

    override def process(feature: PlacedFeature)(using context: FeatureUpdateContext): FeatureProcessResult =
        if (context.onlyUpdate) super.process(feature)
        else Writer.value(PlacedFeature(feature.feature,
            RandomOffsetPlacement(this.xzSpread.process, this.ySpread.process) :: feature.modifiers))
}
