package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import util.{ DataPool, Direction }
import valueprovider.{ BlockStateProvider, IntProvider }

case class GrowingPlantFeatureConfig(val heightDistribution: DataPool[IntProvider], val direction: Direction,
                                     val bodyProvider: BlockStateProvider, val headProvider: BlockStateProvider,
                                     val allowWater: Boolean) extends FeatureConfig derives Codec
