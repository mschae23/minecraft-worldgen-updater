package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import util.{ BlockPos, BlockState }

trait BlockPredicate {
    def predicateType: BlockPredicateType[_]

    def process: BlockPredicate = this
}

object BlockPredicate {
    given Codec[BlockPredicate] = Registry[BlockPredicateType[_]].dispatch(_.predicateType, _.codec)

    BlockPredicateTypes // init

    val MATCHING_AIR = MatchingBlocksBlockPredicate(List(Identifier("minecraft", "air")), BlockPos.ORIGIN)
    val MATCHING_AIR_OR_WATER = MatchingBlocksBlockPredicate(List(Identifier("minecraft", "air"),
        Identifier("minecraft", "water")), BlockPos.ORIGIN)
}

case class BlockPredicateType[P <: BlockPredicate](val codec: Codec[P])

object BlockPredicateType {
    given Registry[BlockPredicateType[_]] = new SimpleRegistry(Identifier("minecraft", "block_predicate_type"))
}

object BlockPredicateTypes {
    val MATCHING_BLOCKS = register("matching_blocks", Codec[MatchingBlocksBlockPredicate])
    val MATCHING_FLUIDS = register("matching_fluids", Codec[MatchingFluidsBlockPredicate])
    val REPLACEABLE = register("replaceable", Codec[ReplaceableBlockPredicate])
    val WOULD_SURVIVE = register("would_survive", Codec[WouldSurviveBlockPredicate])
    val ANY_OF = register("any_of", Codec[AnyOfBlockPredicate])
    val ALL_OF = register("all_of", Codec[AllOfBlockPredicate])
    val NOT = register("not", Codec[NotBlockPredicate])
    val TRUE = register("true", Codec[TrueBlockPredicate.type])

    private def register[P <: BlockPredicate](name: String, codec: Codec[P]): BlockPredicateType[P] = {
        val predicateType = BlockPredicateType(codec)

        predicateType.register(Identifier("minecraft", name))
        predicateType
    }
}

case class MatchingBlocksBlockPredicate(val blocks: List[Identifier], val offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.MATCHING_BLOCKS
}

object MatchingBlocksBlockPredicate {
    given Codec[MatchingBlocksBlockPredicate] = Codec.record {
        val blocks = Codec[List[Identifier]].fieldOf("blocks").forGetter[MatchingBlocksBlockPredicate](_.blocks)
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[MatchingBlocksBlockPredicate](_.offset)

        Codec.build(MatchingBlocksBlockPredicate(blocks.get, offset.get))
    }
}

case class MatchingFluidsBlockPredicate(val fluids: List[Identifier], val offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.MATCHING_FLUIDS
}

object MatchingFluidsBlockPredicate {
    given Codec[MatchingFluidsBlockPredicate] = Codec.record {
        val blocks = Codec[List[Identifier]].fieldOf("fluids").forGetter[MatchingFluidsBlockPredicate](_.fluids)
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[MatchingFluidsBlockPredicate](_.offset)

        Codec.build(MatchingFluidsBlockPredicate(blocks.get, offset.get))
    }
}

case class ReplaceableBlockPredicate(val offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.REPLACEABLE
}

object ReplaceableBlockPredicate {
    given Codec[ReplaceableBlockPredicate] = Codec.record {
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[ReplaceableBlockPredicate](_.offset)

        Codec.build(ReplaceableBlockPredicate(offset.get))
    }
}

case class WouldSurviveBlockPredicate(val offset: BlockPos, val state: BlockState) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.WOULD_SURVIVE
}

object WouldSurviveBlockPredicate {
    given Codec[WouldSurviveBlockPredicate] = Codec.record {
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[WouldSurviveBlockPredicate](_.offset)
        val state = Codec[BlockState].fieldOf("state").forGetter[WouldSurviveBlockPredicate](_.state)

        Codec.build(WouldSurviveBlockPredicate(offset.get, state.get))
    }
}

case class AnyOfBlockPredicate(val predicates: List[BlockPredicate]) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.ANY_OF

    override def process: BlockPredicate = this.predicates.map(_.process) match {
        case predicate :: Nil => predicate.process
        case Nil => NotBlockPredicate(TrueBlockPredicate).process
        case predicates if predicates.contains(TrueBlockPredicate) => TrueBlockPredicate.process
        case predicates if predicates.exists(_ match {
            case AnyOfBlockPredicate(_) => true
            case NotBlockPredicate(AllOfBlockPredicate(_)) => true
            case _ => false
        }) =>
            AnyOfBlockPredicate(predicates.flatMap(_ match {
                case AnyOfBlockPredicate(predicates) => predicates
                case NotBlockPredicate(AllOfBlockPredicate(predicates)) =>
                    predicates.map(NotBlockPredicate(_)).map(_.process)
                case other => List(other)
            })).process

        case predicates => AnyOfBlockPredicate(predicates)
    }
}

case class AllOfBlockPredicate(val predicates: List[BlockPredicate]) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.ALL_OF

    override def process: BlockPredicate = this.predicates.map(_.process) match {
        case predicate :: Nil => predicate.process
        case Nil => TrueBlockPredicate
        case predicates if predicates.contains(TrueBlockPredicate) => AllOfBlockPredicate(predicates
            .filter(_ != TrueBlockPredicate)).process
        case predicates if predicates.exists(_ match {
            case AllOfBlockPredicate(_) => true
            case NotBlockPredicate(AnyOfBlockPredicate(_)) => true
            case _ => false
        }) =>
            AllOfBlockPredicate(predicates.flatMap(_ match {
                case AllOfBlockPredicate(predicates) => predicates
                case NotBlockPredicate(AnyOfBlockPredicate(predicates)) =>
                    predicates.map(NotBlockPredicate(_)).map(_.process)
                case other => List(other)
            })).process

        case predicates => AllOfBlockPredicate(predicates)
    }
}

case class NotBlockPredicate(val predicate: BlockPredicate) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.NOT

    override def process: BlockPredicate = this.predicate.process match {
        case NotBlockPredicate(predicate) => predicate.process

        case predicate => NotBlockPredicate(predicate)
    }
}

case object TrueBlockPredicate extends BlockPredicate {
    override def predicateType: BlockPredicateType[_] = BlockPredicateTypes.TRUE

    given Codec[TrueBlockPredicate.type] = Codec.unit(TrueBlockPredicate)
}
