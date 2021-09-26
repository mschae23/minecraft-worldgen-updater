package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import util.{ BlockState, HeightmapType }

case class SurfaceRelativeThresholdDecoratorConfig(heightmap: HeightmapType,
                                                   minInclusive: Int, maxInclusive: Int) extends DecoratorConfig derives Codec
