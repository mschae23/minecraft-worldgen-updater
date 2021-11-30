package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.*
import util.*

trait BlockStateProvider {
    val providerType: BlockStateProviderType[_]

    val process: BlockStateProvider = this
}

object BlockStateProvider {
    given Codec[BlockStateProvider] = Registry[BlockStateProviderType[_]].dispatch(_.providerType, _.codec)

    BlockStateProviderTypes // init
}

case class BlockStateProviderType[P <: BlockStateProvider](codec: Codec[P])

object BlockStateProviderType {
    given Registry[BlockStateProviderType[_]] = new SimpleRegistry(Identifier("minecraft", "blockstate_provider_type"))
}

object BlockStateProviderTypes {
    val SIMPLE_STATE_PROVIDER = register("simple_state_provider", Codec[SimpleBlockStateProvider])
    val WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", Codec[WeightedBlockStateProvider])
    val NOISE_THRESHOLD_PROVIDER = register("noise_threshold_provider", Codec[NoiseThresholdBlockStateProvider])
    val NOISE_PROVIDER = register("noise_provider", Codec[NoiseBlockStateProvider])
    val DUAL_NOISE_PROVIDER = register("dual_noise_provider", Codec[DualNoiseBlockStateProvider])
    val ROTATED_BLOCK_PROVIDER = register("rotated_block_provider", Codec[RotatedBlockProvider])
    val RANDOMIZED_INT_STATE_PROVIDER = register("randomized_int_state_provider", Codec[RandomizedIntStateProvider])

    private def register[P <: BlockStateProvider](name: String, codec: Codec[P]): BlockStateProviderType[P] = {
        val providerType = BlockStateProviderType(codec)

        providerType.register(Identifier("minecraft", name))
        providerType
    }
}

case class SimpleBlockStateProvider(state: BlockState) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.SIMPLE_STATE_PROVIDER
}

case class WeightedBlockStateProvider(entries: DataPool[BlockState]) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.WEIGHTED_STATE_PROVIDER
}

case class NoiseThresholdBlockStateProvider(seed: Long, noise: NoiseParameters, scale: Float,
                                            threshold: Float, highChance: Float, defaultState: BlockState,
                                            lowStates: List[BlockState],
                                            highStates: List[BlockState]) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.NOISE_THRESHOLD_PROVIDER

    override val process: BlockStateProvider = if ((lowStates.isEmpty || lowStates.iterator.forall(_ == defaultState))
        && ((highStates.isEmpty || highStates.iterator.forall(_ == defaultState)))) SimpleBlockStateProvider(defaultState)
    else this
}

case class NoiseBlockStateProvider(seed: Long, noise: NoiseParameters, scale: Float,
                                   states: List[BlockState]) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.NOISE_PROVIDER

    override val process: BlockStateProvider = if (this.states.length == 1)
        SimpleBlockStateProvider(this.states.head) else this
}

case class DualNoiseBlockStateProvider(variety: Range[Int], slowNoise: NoiseParameters, slowScale: Float,
                                       seed: Long, noise: NoiseParameters, scale: Float,
                                       states: List[BlockState]) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.DUAL_NOISE_PROVIDER

    override val process: BlockStateProvider = if (this.states.length == 1)
        SimpleBlockStateProvider(this.states.head) else this
}

case class RotatedBlockProvider(state: BlockState) extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.ROTATED_BLOCK_PROVIDER
}

case class RandomizedIntStateProvider(source: BlockStateProvider, property: String, values: IntProvider)
    extends BlockStateProvider derives Codec {
    override val providerType: BlockStateProviderType[_] = BlockStateProviderTypes.RANDOMIZED_INT_STATE_PROVIDER
}
