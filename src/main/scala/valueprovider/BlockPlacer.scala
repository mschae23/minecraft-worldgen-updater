package de.martenschaefer.minecraft.worldgenupdater
package valueprovider

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier
import util.BlockPos

trait BlockPlacer {
    val placerType: BlockPlacerType[_]
}

object BlockPlacer {
    given Codec[BlockPlacer] = Registry[BlockPlacerType[_]].dispatch(_.placerType, _.codec)

    BlockPlacerTypes // init
}

case class BlockPlacerType[P <: BlockPlacer](val codec: Codec[P])

object BlockPlacerType {
    given Registry[BlockPlacerType[_]] = new SimpleRegistry(Identifier("minecraft", "block_placer_type"))
}

object BlockPlacerTypes {
    val SIMPLE_BLOCK_PLACER = register("simple_block_placer", Codec[SimpleBlockPlacer.type])
    val DOUBLE_PLANT_PLACER = register("double_plant_placer", Codec[SimpleBlockPlacer.type])
    val COLUMN_PLACER = register("column_placer", Codec[ColumnPlacer])

    private def register[P <: BlockPlacer](name: String, codec: Codec[P]): BlockPlacerType[P] = {
        val placerType = BlockPlacerType(codec)

        placerType.register(Identifier("minecraft", name))
        placerType
    }
}

case object SimpleBlockPlacer extends BlockPlacer {
    override val placerType: BlockPlacerType[_] = BlockPlacerTypes.SIMPLE_BLOCK_PLACER

    given Codec[SimpleBlockPlacer.type] = Codec.unit(SimpleBlockPlacer)
}

case object DoublePlantPlacer extends BlockPlacer {
    override val placerType: BlockPlacerType[_] = BlockPlacerTypes.SIMPLE_BLOCK_PLACER

    given Codec[DoublePlantPlacer.type] = Codec.unit(DoublePlantPlacer)
}

case class ColumnPlacer(val size: IntProvider) extends BlockPlacer derives Codec {
    override val placerType: BlockPlacerType[_] = BlockPlacerTypes.SIMPLE_BLOCK_PLACER
}
