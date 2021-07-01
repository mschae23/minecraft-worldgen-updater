package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import decorator.{ DecoratorConfig, DefaultDecoratorConfig }
import util.HeightmapType

case class HeightmapDecoratorConfig(val heightmap: HeightmapType) extends DecoratorConfig

object HeightmapDecoratorConfig {
    val old1Codec =
        Codec[DefaultDecoratorConfig].xmap(_ => HeightmapDecoratorConfig(HeightmapType.MotionBlocking))(_ => null)

    given Codec[HeightmapDecoratorConfig] = Codec.derived[HeightmapDecoratorConfig]
        .flatOrElse(old1Codec)
}
