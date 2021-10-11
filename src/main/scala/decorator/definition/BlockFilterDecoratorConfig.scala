package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import decorator.DecoratorConfig
import decorator.definition.BlockFilterDecoratorConfig.Old1
import util.BlockPos
import valueprovider.{ BlockPredicate, TrueBlockPredicate }

case class BlockFilterDecoratorConfig(val predicate: BlockPredicate,
                                      val old1: Option[Old1] = None) extends DecoratorConfig

object BlockFilterDecoratorConfig {
    case class Old1(allowed: List[Identifier], disallowed: List[Identifier], offset: BlockPos) derives Codec

    val old1Codec: Codec[BlockFilterDecoratorConfig] = Codec[Old1].xmap(old1 =>
        BlockFilterDecoratorConfig(TrueBlockPredicate, Some(old1)))(_ => null)

    given Codec[BlockFilterDecoratorConfig] = Codec.derived[BlockFilterDecoratorConfig]
        .flatOrElse(old1Codec)
}
