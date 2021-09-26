package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import feature.{ Feature, FeatureProcessResult }

case object OreFeature extends Feature(Codec[OreFeatureConfig]) {
    override def process(config: OreFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        var processed = super.process(config, context)

        if (config.size < 0 || config.size > 64)
            processed = processed.mapWritten(warnings => ValidationError(
                path => s"$path must be between 0 and 64: ${ config.size }",
                List(ElementNode.Name("config"), ElementNode.Name("size"))) :: warnings)

        if (config.discardChanceOnAirExposure < 0 || config.discardChanceOnAirExposure > 1)
            processed = processed.mapWritten(warnings => ValidationError(
                path => s"$path must be between 0 and 1: ${ config.discardChanceOnAirExposure }",
                List(ElementNode.Name("config"), ElementNode.Name("discard_chance_on_air_exposure"))) :: warnings)

        processed
    }
}
