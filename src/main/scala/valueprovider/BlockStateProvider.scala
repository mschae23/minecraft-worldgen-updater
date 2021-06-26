package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import util._

trait BlockStateProvider {
    val providerType: BlockStateProviderType[_]

    val process: BlockStateProvider = this
}

object BlockStateProvider {
    given Codec[BlockStateProvider] = Registry[BlockStateProviderType[_]].dispatch(_.providerType, _.codec)

    BlockStateProviderTypes // init
}

case class BlockStateProviderType[P <: BlockStateProvider](val codec: Codec[P])

object BlockStateProviderType {
    given Registry[BlockStateProviderType[_]] = new SimpleRegistry(Identifier("minecraft", "blockstate_provider_type"))
}

object BlockStateProviderTypes {
    val SIMPLE_STATE_PROVIDER = register("simple_state_provider", Codec[SimpleBlockStateProvider])
    val WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", Codec[WeightedBlockStateProvider])
    val PLAINS_FLOWER_PROVIDER = register("plains_flower_provider", Codec[PlainsFlowerProvider])
    val FOREST_FLOWER_PROVIDER = register("forest_flower_provider", Codec[ForestFlowerProvider])
    val ROTATED_BLOCK_PROVIDER = register("rotated_block_provider", Codec[RotatedBlockProvider])
    val RANDOMIZED_INT_STATE_PROVIDER = register("randomized_int_state_provider", Codec[RandomizedIntStateProvider])

    private def register[P <: BlockStateProvider](name: String, codec: Codec[P]): BlockStateProviderType[P] = {
        val providerType = BlockStateProviderType(codec)

        providerType.register(Identifier("minecraft", name))
        providerType
    }
}

case class SimpleBlockStateProvider(val state: BlockState) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.SIMPLE_STATE_PROVIDER
}

case class WeightedBlockStateProvider(val entries: DataPool[BlockState]) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.WEIGHTED_STATE_PROVIDER
}

case class PlainsFlowerProvider() extends BlockStateProvider {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.PLAINS_FLOWER_PROVIDER
}

object PlainsFlowerProvider {
    lazy val INSTANCE = new PlainsFlowerProvider()

    def apply: PlainsFlowerProvider = INSTANCE

    given Codec[PlainsFlowerProvider] = Codec.unit(() => INSTANCE)
}

case class ForestFlowerProvider() extends BlockStateProvider {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.FOREST_FLOWER_PROVIDER
}

object ForestFlowerProvider {
    lazy val INSTANCE = new ForestFlowerProvider()

    def apply: ForestFlowerProvider = INSTANCE

    given Codec[ForestFlowerProvider] = Codec.unit(() => INSTANCE)
}

case class RotatedBlockProvider(val state: BlockState) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.ROTATED_BLOCK_PROVIDER
}

case class RandomizedIntStateProvider(val source: BlockStateProvider, val property: String, val values: IntProvider)
    extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.RANDOMIZED_INT_STATE_PROVIDER
}
