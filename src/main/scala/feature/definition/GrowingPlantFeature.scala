package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.DataResult.*
import feature.definition.BlockColumnFeatureConfig.Layer
import feature.{ Feature, FeatureProcessResult, Features }
import util.{ DataPool, Weighted }
import valueprovider.{ ConstantIntProvider, WeightedListIntProvider }

object GrowingPlantFeature extends Feature(Codec[GrowingPlantFeatureConfig]) {
    override def process(config: GrowingPlantFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        Features.BLOCK_COLUMN.process(BlockColumnFeatureConfig(List(Layer(
            WeightedListIntProvider(DataPool(config.heightDistribution.entries.map(weighted => Weighted.Present(
                (if (context.onlyUpdate) weighted.data else weighted.data.process)
                    .map(_ - 1, _ - 1), weighted.weight)))), config.bodyProvider), Layer(ConstantIntProvider(1), config.headProvider)),
            config.direction, config.allowWater, true), context)
    }
}
