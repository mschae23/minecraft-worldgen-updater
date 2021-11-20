package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.Codec
import feature.FeatureConfig
import util.{ DataPool, Direction }
import valueprovider.{ BlockStateProvider, IntProvider }

case class GrowingPlantFeatureConfig(heightDistribution: DataPool[IntProvider], direction: Direction,
                                     bodyProvider: BlockStateProvider, headProvider: BlockStateProvider,
                                     allowWater: Boolean) extends FeatureConfig derives Codec
