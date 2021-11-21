package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.Decorator
import feature.placement.PlacedFeature
import feature.placement.definition.{ HeightRangePlacement, HeightmapPlacement }
import feature.{ ConfiguredFeature, FeatureProcessResult }
import valueprovider.ConstantIntProvider
import cats.data.Writer

case object HeightmapDecorator extends Decorator(Codec[HeightmapDecoratorConfig]) {
    override def process(config: HeightmapDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        HeightRangePlacement.processHeightReplacingModifier(feature, HeightmapPlacement(config.heightmap)
            .process(feature)(using context))(using context)
}
