package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import feature.definition.BlockColumnFeatureConfig.Layer
import util.{ BlockPos, Direction }
import valueprovider.{ BlockPredicate, BlockStateProvider, IntProvider, MatchingBlocksBlockPredicate }

case class BlockColumnFeatureConfig(layers: List[Layer], direction: Direction,
                                    allowedPlacement: BlockPredicate, prioritizeTip: Boolean) extends FeatureConfig

object BlockColumnFeatureConfig {
    case class Layer(height: IntProvider, provider: BlockStateProvider) derives Codec {
        def process: Layer = Layer(this.height.process, this.provider.process)
    }

    case class Old1(layers: List[Layer], direction: Direction, allowWater: Boolean, prioritizeTip: Boolean) derives Codec

    val old1Codec: Codec[BlockColumnFeatureConfig] = Codec[Old1].xmap(old1 => BlockColumnFeatureConfig(old1.layers, old1.direction,
        if (old1.allowWater) BlockPredicate.MATCHING_AIR_OR_WATER else BlockPredicate.MATCHING_AIR, old1.prioritizeTip))(_ => null)

    given Codec[BlockColumnFeatureConfig] = Codec.alternatives(List(Codec.derived[BlockColumnFeatureConfig], old1Codec))
}
