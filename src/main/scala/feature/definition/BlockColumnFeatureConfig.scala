package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import feature.definition.BlockColumnFeatureConfig.Layer
import util.Direction
import valueprovider.{ BlockStateProvider, IntProvider }

case class BlockColumnFeatureConfig(val layers: List[Layer], val direction: Direction,
                                    val allowWater: Boolean, val prioritizeTip: Boolean) extends FeatureConfig derives Codec

object BlockColumnFeatureConfig {
    case class Layer(val height: IntProvider, val provider: BlockStateProvider) derives Codec
}
