package de.martenschaefer.minecraft.worldgenupdater
package feature.definition.tree

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.*
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.{ BlockStateProvider, IntProvider }
import util.*

trait TreeDecorator {
    val decoratorType: TreeDecoratorType[_]

    def process: TreeDecorator = this
}

object TreeDecorator {
    given Codec[TreeDecorator] = Registry[TreeDecoratorType[_]].dispatch(_.decoratorType, _.codec)
}

case class TreeDecoratorType[P <: TreeDecorator](val codec: Codec[P])

object TreeDecoratorType {
    given Registry[TreeDecoratorType[_]] = new SimpleRegistry(Identifier("minecraft", "tree_decorator_type"))

    val TRUNK_VINE = register("trunk_vine", Codec[TrunkVineTreeDecorator.type])
    val LEAVE_VINE = register("leave_vine", Codec[LeavesVineTreeDecorator.type])
    val COCOA = register("cocoa", Codec[CocoaBeansTreeDecorator])
    val BEEHIVE = register("beehive", Codec[BeehiveTreeDecorator])
    val ALTER_GROUND = register("alter_ground", Codec[AlterGroundTreeDecorator])

    private def register[P <: TreeDecorator](name: String, codec: Codec[P]): TreeDecoratorType[P] = {
        val decoratorType = TreeDecoratorType(codec)

        decoratorType.register(Identifier("minecraft", name))
        decoratorType
    }
}

case object TrunkVineTreeDecorator extends TreeDecorator {
    override val decoratorType: TreeDecoratorType[_] = TreeDecoratorType.TRUNK_VINE

    given Codec[TrunkVineTreeDecorator.type] = Codec.unit(TrunkVineTreeDecorator)
}

case object LeavesVineTreeDecorator extends TreeDecorator {
    override val decoratorType: TreeDecoratorType[_] = TreeDecoratorType.LEAVE_VINE

    given Codec[LeavesVineTreeDecorator.type] = Codec.unit(LeavesVineTreeDecorator)
}

case class CocoaBeansTreeDecorator(val probability: Float) extends TreeDecorator derives Codec {
    override val decoratorType: TreeDecoratorType[_] = TreeDecoratorType.COCOA
}

case class BeehiveTreeDecorator(val probability: Float) extends TreeDecorator derives Codec {
    override val decoratorType: TreeDecoratorType[_] = TreeDecoratorType.BEEHIVE
}

case class AlterGroundTreeDecorator(val provider: BlockStateProvider) extends TreeDecorator derives Codec {
    override val decoratorType: TreeDecoratorType[_] = TreeDecoratorType.ALTER_GROUND

    override def process: TreeDecorator = AlterGroundTreeDecorator(this.provider.process)
}
