package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.{ Decorator, DecoratorConfig }
import feature.FeatureProcessResult
import feature.placement.PlacedFeature
import feature.placement.definition.RandomOffsetPlacement
import valueprovider.IntProvider

case class ScatterDecoratorConfig(xzSpread: IntProvider, ySpread: IntProvider) extends DecoratorConfig derives Codec

case object ScatterDecorator extends Decorator(Codec[ScatterDecoratorConfig]) {
    override def process(config: ScatterDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        RandomOffsetPlacement(config.xzSpread, config.ySpread).process(feature)(using context)
}
