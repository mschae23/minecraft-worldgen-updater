package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import decorator.DecoratorConfig
import decorator.definition.BlockFilterDecoratorConfig.Old1
import util.*
import util.MinecraftIdentifier.{ toIdentifier, toMinecraft }
import valueprovider.{ BlockPredicate, TrueBlockPredicate }

case class BlockFilterDecoratorConfig(predicate: BlockPredicate,
                                      old1: Option[Old1] = None) extends DecoratorConfig

object BlockFilterDecoratorConfig {
    case class Old1(allowed: List[MinecraftIdentifier], disallowed: List[MinecraftIdentifier], offset: BlockPos)

    given Codec[Old1] = Codec.record {
        val allowed = Codec[List[MinecraftIdentifier]].orElse(List.empty).fieldOf("allowed").forGetter[Old1](_.allowed)
        val disallowed = Codec[List[MinecraftIdentifier]].orElse(List.empty).fieldOf("disallowed").forGetter[Old1](_.disallowed)
        val offset = Codec[BlockPos].fieldOf("offset").forGetter[Old1](_.offset)

        Codec.build(Old1(allowed.get, disallowed.get, offset.get))
    }

    val old1Codec: Codec[BlockFilterDecoratorConfig] = Codec[Old1].xmap(old1 =>
        BlockFilterDecoratorConfig(TrueBlockPredicate, Some(old1)))(_ => null)

    given Codec[BlockFilterDecoratorConfig] = Codec.derived[BlockFilterDecoratorConfig]
        .flatOrElse(old1Codec)
}
