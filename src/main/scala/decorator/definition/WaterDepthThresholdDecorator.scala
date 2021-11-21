package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.Decorator
import feature.{ ConfiguredFeature, FeatureProcessResult }
import feature.placement.PlacedFeature
import feature.placement.definition.SurfaceWaterDepthFilterPlacement

case object WaterDepthThresholdDecorator extends Decorator(Codec[WaterDepthThresholdDecoratorConfig]) {
    override def process(config: WaterDepthThresholdDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        SurfaceWaterDepthFilterPlacement(config.maxWaterDepth).process(feature)(using context)
}
