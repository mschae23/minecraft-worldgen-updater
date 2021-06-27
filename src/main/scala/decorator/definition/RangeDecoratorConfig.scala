package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.DecoratorConfig
import valueprovider.{ HeightProvider, UniformHeightProvider }
import util.YOffset

case class RangeDecoratorConfig(height: HeightProvider) extends DecoratorConfig

object RangeDecoratorConfig {
    case class Old1(val bottomOffset: Int, val topOffset: Int, val maximum: Int) derives Codec

    val old1Codec: Codec[RangeDecoratorConfig] = Codec[Old1].xmap(old1 => RangeDecoratorConfig(
        UniformHeightProvider(YOffset.AboveBottom(old1.bottomOffset), YOffset.AboveBottom(old1.maximum - old1.topOffset - 1 + old1.bottomOffset))))(
        _ => null)

    given Codec[RangeDecoratorConfig] = Codec.derived[RangeDecoratorConfig].flatOrElse(old1Codec)
}
