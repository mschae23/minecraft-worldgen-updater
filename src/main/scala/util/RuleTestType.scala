package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._

trait RuleTest {
    val ruleType: RuleTestType[_]

    val process: RuleTest = this
}

object RuleTest {
    given Codec[RuleTest] = Registry[RuleTestType[_]].dispatch("predicate_type", _.ruleType, _.codec)
}

trait RuleTestType[P <: RuleTest] {
    val codec: Codec[P]
}

object RuleTestType {
    given Registry[RuleTestType[_]] = new SimpleRegistry(Identifier("minecraft", "rule_test_type"))

    val ALWAYS_TRUE = register("always_true", Codec[AlwaysTrueRuleTest])
    val BLOCK_MATCH = register("block_match", Codec[BlockMatchRuleTest])
    val BLOCKSTATE_MATCH = register("blockstate_match", Codec[BlockStateMatchRuleTest])
    val TAG_MATCH = register("tag_match", Codec[TagMatchRuleTest])
    val RANDOM_BLOCK_MATCH = register("random_block_match", Codec[RandomBlockMatchRuleTest])
    val RANDOM_BLOCKSTATE_MATCH = register("random_blockstate_match", Codec[RandomBlockStateMatchRuleTest])

    private def register[P <: RuleTest](name: String, ruleCodec: Codec[P]): RuleTestType[P] = {
        val ruleType = new RuleTestType[P] {
            val codec: Codec[P] = ruleCodec
        }

        ruleType.register(Identifier("minecraft", name))
        ruleType
    }
}

case class AlwaysTrueRuleTest() extends RuleTest {
    override val ruleType: RuleTestType[_] = RuleTestType.ALWAYS_TRUE
}

object AlwaysTrueRuleTest {
    lazy val INSTANCE = AlwaysTrueRuleTest()

    given Codec[AlwaysTrueRuleTest] = Codec.unit(() => INSTANCE)
}

case class BlockMatchRuleTest(val block: Identifier) extends RuleTest derives Codec {
    override val ruleType: RuleTestType[_] = RuleTestType.BLOCK_MATCH
}

case class BlockStateMatchRuleTest(val blockState: BlockState) extends RuleTest derives Codec {
    override val ruleType: RuleTestType[_] = RuleTestType.BLOCKSTATE_MATCH
}

case class TagMatchRuleTest(val tag: Identifier) extends RuleTest derives Codec {
    override val ruleType: RuleTestType[_] = RuleTestType.TAG_MATCH
}

case class RandomBlockMatchRuleTest(val block: Identifier, val probability: Float) extends RuleTest derives Codec {
    override val ruleType: RuleTestType[_] = RuleTestType.RANDOM_BLOCK_MATCH
}

case class RandomBlockStateMatchRuleTest(val blockState: BlockState, val probability: Float) extends RuleTest derives Codec {
    override val ruleType: RuleTestType[_] = RuleTestType.RANDOM_BLOCKSTATE_MATCH
}
