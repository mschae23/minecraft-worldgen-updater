package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import util.BlockPos

trait BlockPredicate {
    val predicateType: BlockPredicateType[_]

    val process: BlockPredicate = this
}

object BlockPredicate {
    given Codec[BlockPredicate] = Registry[BlockPredicateType[_]].dispatch(_.predicateType, _.codec)

    BlockPredicateTypes // init
}

case class BlockPredicateType[P <: BlockPredicate](val codec: Codec[P])

object BlockPredicateType {
    given Registry[BlockPredicateType[_]] = new SimpleRegistry(Identifier("minecraft", "block_predicate_type"))
}

object BlockPredicateTypes {
    val MATCHING_BLOCKS = register("matching_blocks", Codec[MatchingBlocksBlockPredicate])
    val MATCHING_FLUIDS = register("matching_fluids", Codec[MatchingFluidsBlockPredicate])
    val REPLACEABLE = register("replaceable", Codec[ReplaceableBlockPredicate.type])
    val ANY_OF = register("any_of", Codec[AnyOfBlockPredicate])
    val ALL_OF = register("all_of", Codec[AllOfBlockPredicate])
    val NOT = register("not", Codec[NotBlockPredicate])

    private def register[P <: BlockPredicate](name: String, codec: Codec[P]): BlockPredicateType[P] = {
        val predicateType = BlockPredicateType(codec)

        predicateType.register(Identifier("minecraft", name))
        predicateType
    }
}

case class MatchingBlocksBlockPredicate(val blocks: List[Identifier], val offset: BlockPos) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.MATCHING_BLOCKS
}

case class MatchingFluidsBlockPredicate(val fluids: List[Identifier], val offset: BlockPos) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.MATCHING_FLUIDS
}

case object ReplaceableBlockPredicate extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.REPLACEABLE

    given Codec[ReplaceableBlockPredicate.type] = Codec.unit(ReplaceableBlockPredicate)
}

case class AnyOfBlockPredicate(val predicates: List[BlockPredicate]) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.ANY_OF

    override val process: BlockPredicate = this.predicates match {
        case predicate :: Nil => predicate.process

        case _ => AnyOfBlockPredicate(this.predicates.map(_.process))
    }
}

case class AllOfBlockPredicate(val predicates: List[BlockPredicate]) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.ALL_OF

    this.predicates match {
        case predicate :: Nil => predicate.process

        case _ => AllOfBlockPredicate(this.predicates.map(_.process))
    }
}

case class NotBlockPredicate(val predicate: BlockPredicate) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.NOT

    this.predicate match {
        case NotBlockPredicate(predicate) => predicate.process

        case _ => NotBlockPredicate(this.predicate.process)
    }
}
