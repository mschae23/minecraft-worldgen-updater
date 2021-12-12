package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import java.util.Objects
import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import util.*

trait BlockPredicate {
    def predicateType: BlockPredicateType[_]

    def process: BlockPredicate = this
}

object BlockPredicate {
    given Codec[BlockPredicate] = Registry[BlockPredicateType[_]].dispatch(_.predicateType, _.codec)

    BlockPredicateTypes // init

    val MATCHING_AIR = MatchingBlocksBlockPredicate(List(MinecraftIdentifier("minecraft", "air")), BlockPos.ORIGIN)
    val MATCHING_AIR_OR_WATER = MatchingBlocksBlockPredicate(List(MinecraftIdentifier("minecraft", "air"),
        MinecraftIdentifier("minecraft", "water")), BlockPos.ORIGIN)

    def hasTruePredicate(predicates: List[BlockPredicate]): Boolean =
        predicates.contains(TrueBlockPredicate) || predicates.uniquePairs.exists(isAlwaysTruePair)

    def isAlwaysTruePair(a: BlockPredicate, b: BlockPredicate): Boolean = a match {
        case NotBlockPredicate(predicateA) => b == predicateA
        case _ => b match {
            case NotBlockPredicate(predicateB) => a == predicateB
            case _ => false
        }
    }
}

case class BlockPredicateType[P <: BlockPredicate](codec: Codec[P])

object BlockPredicateType {
    given Registry[BlockPredicateType[_]] = new SimpleRegistry(Identifier("minecraft", "block_predicate_type"))
}

object BlockPredicateTypes {
    val MATCHING_BLOCKS = register("matching_blocks", Codec[MatchingBlocksBlockPredicate])
    val MATCHING_BLOCK_TAG = register("matching_block_tag", Codec[MatchingBlockTagBlockPredicate])
    val MATCHING_FLUIDS = register("matching_fluids", Codec[MatchingFluidsBlockPredicate])
    val HAS_STURDY_FACE = register("has_sturdy_face", Codec[HasSturdyFaceBlockPredicate])
    val REPLACEABLE = register("replaceable", Codec[ReplaceableBlockPredicate])
    val SOLID = register("solid", Codec[SolidBlockPredicate])
    val WOULD_SURVIVE = register("would_survive", Codec[WouldSurviveBlockPredicate])
    val ANY_OF = register("any_of", Codec[AnyOfBlockPredicate])
    val ALL_OF = register("all_of", Codec[AllOfBlockPredicate])
    val NOT = register("not", Codec[NotBlockPredicate])
    val TRUE = register("true", Codec[TrueBlockPredicate.type])
    val FALSE = registerCustom("false", Codec[FalseBlockPredicate.type])

    private def register[P <: BlockPredicate](name: String, codec: Codec[P]): BlockPredicateType[P] = {
        val predicateType = BlockPredicateType(codec)

        predicateType.register(Identifier("minecraft", name))
        predicateType
    }

    private def registerCustom[P <: BlockPredicate](name: String, codec: Codec[P]): BlockPredicateType[P] = {
        val predicateType = BlockPredicateType(codec)

        predicateType.register(Identifier(UpdaterMain.NAMESPACE, name))
        predicateType
    }
}

case class MatchingBlocksBlockPredicate(blocks: List[MinecraftIdentifier], offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.MATCHING_BLOCKS
}

object MatchingBlocksBlockPredicate {
    given Codec[MatchingBlocksBlockPredicate] = Codec.record {
        val blocks = Codec[List[MinecraftIdentifier]].fieldOf("blocks").forGetter[MatchingBlocksBlockPredicate](_.blocks)
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[MatchingBlocksBlockPredicate](_.offset)

        Codec.build(MatchingBlocksBlockPredicate(blocks.get, offset.get))
    }
}

case class MatchingBlockTagBlockPredicate(tag: MinecraftIdentifier, offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.MATCHING_BLOCK_TAG
}

object MatchingBlockTagBlockPredicate {
    given Codec[MatchingBlockTagBlockPredicate] = Codec.record {
        val tag = Codec[MinecraftIdentifier].fieldOf("tag").forGetter[MatchingBlockTagBlockPredicate](_.tag)
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[MatchingBlockTagBlockPredicate](_.offset)

        Codec.build(MatchingBlockTagBlockPredicate(tag.get, offset.get))
    }
}

case class MatchingFluidsBlockPredicate(fluids: List[MinecraftIdentifier], offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.MATCHING_FLUIDS
}

object MatchingFluidsBlockPredicate {
    given Codec[MatchingFluidsBlockPredicate] = Codec.record {
        val blocks = Codec[List[MinecraftIdentifier]].fieldOf("fluids").forGetter[MatchingFluidsBlockPredicate](_.fluids)
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[MatchingFluidsBlockPredicate](_.offset)

        Codec.build(MatchingFluidsBlockPredicate(blocks.get, offset.get))
    }
}

case class HasSturdyFaceBlockPredicate(offset: BlockPos, direction: Direction) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.HAS_STURDY_FACE
}

object HasSturdyFaceBlockPredicate {
    given Codec[HasSturdyFaceBlockPredicate] = Codec.record {
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[HasSturdyFaceBlockPredicate](_.offset)
        val direction = Codec[Direction].fieldOf("direction").forGetter[HasSturdyFaceBlockPredicate](_.direction)

        Codec.build(HasSturdyFaceBlockPredicate(offset.get, direction.get))
    }
}

case class ReplaceableBlockPredicate(offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.REPLACEABLE
}

object ReplaceableBlockPredicate {
    given Codec[ReplaceableBlockPredicate] = Codec.record {
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[ReplaceableBlockPredicate](_.offset)

        Codec.build(ReplaceableBlockPredicate(offset.get))
    }
}

case class SolidBlockPredicate(offset: BlockPos) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.SOLID
}

object SolidBlockPredicate {
    given Codec[SolidBlockPredicate] = Codec.record {
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[SolidBlockPredicate](_.offset)

        Codec.build(SolidBlockPredicate(offset.get))
    }
}

case class WouldSurviveBlockPredicate(offset: BlockPos, state: BlockState) extends BlockPredicate {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.WOULD_SURVIVE
}

object WouldSurviveBlockPredicate {
    given Codec[WouldSurviveBlockPredicate] = Codec.record {
        val offset = Codec[BlockPos].orElse(BlockPos.ORIGIN).fieldOf("offset").forGetter[WouldSurviveBlockPredicate](_.offset)
        val state = Codec[BlockState].fieldOf("state").forGetter[WouldSurviveBlockPredicate](_.state)

        Codec.build(WouldSurviveBlockPredicate(offset.get, state.get))
    }
}

case class AnyOfBlockPredicate(predicates: List[BlockPredicate]) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.ANY_OF

    override def process: BlockPredicate = this.predicates.map(_.process).distinct match {
        case predicate :: Nil => predicate.process
        case Nil => NotBlockPredicate(TrueBlockPredicate).process
        case predicates if BlockPredicate.hasTruePredicate(predicates) => TrueBlockPredicate.process
        case predicates if predicates.exists(_ match {
            case AnyOfBlockPredicate(_) => true
            case NotBlockPredicate(AllOfBlockPredicate(_)) => true
            case FalseBlockPredicate => true
            case _ => false
        }) =>
            AnyOfBlockPredicate(predicates.flatMap(_ match {
                case AnyOfBlockPredicate(predicates) => predicates
                case NotBlockPredicate(AllOfBlockPredicate(predicates)) =>
                    predicates.map(NotBlockPredicate.apply).map(_.process)
                case FalseBlockPredicate => List.empty
                case other => List(other)
            })).process

        case predicates => AnyOfBlockPredicate(predicates)
    }
}

case class AllOfBlockPredicate(predicates: List[BlockPredicate]) extends BlockPredicate derives Codec {
    override val predicateType: BlockPredicateType[_] = BlockPredicateTypes.ALL_OF

    override def process: BlockPredicate = this.predicates.map(_.process).distinct match {
        case predicate :: Nil => predicate.process
        case Nil => TrueBlockPredicate
        case predicates if predicates.contains(TrueBlockPredicate) => AllOfBlockPredicate(predicates
            .filter(_ != TrueBlockPredicate)).process
        case predicates if predicates.contains(NotBlockPredicate(TrueBlockPredicate)) =>
            FalseBlockPredicate.process
        case predicates if predicates.exists(_ match {
            case AllOfBlockPredicate(_) => true
            case NotBlockPredicate(AnyOfBlockPredicate(_)) => true
            case _ => false
        }) =>
            AllOfBlockPredicate(predicates.flatMap(_ match {
                case AllOfBlockPredicate(predicates) => predicates
                case NotBlockPredicate(AnyOfBlockPredicate(predicates)) =>
                    predicates.map(NotBlockPredicate.apply).map(_.process)
                case other => List(other)
            })).process
        case predicates if BlockPredicate.hasTruePredicate(predicates) =>
            FalseBlockPredicate.process

        case predicates => AllOfBlockPredicate(predicates)
    }
}

case class NotBlockPredicate(predicate: BlockPredicate) extends BlockPredicate derives Codec {
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

case object FalseBlockPredicate extends BlockPredicate {
    override def predicateType: BlockPredicateType[_] = BlockPredicateTypes.FALSE

    given Codec[FalseBlockPredicate.type] = Codec.unit(FalseBlockPredicate)

    override def process: BlockPredicate = NotBlockPredicate(TrueBlockPredicate).process

    override def equals(o: Any): Boolean = (o.isInstanceOf[AnyRef] && FalseBlockPredicate.eq(o.asInstanceOf[AnyRef]))
        || NotBlockPredicate(TrueBlockPredicate) == o

    def unapply(predicate: BlockPredicate): Boolean = equals(predicate)
}
