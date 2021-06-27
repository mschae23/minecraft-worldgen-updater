package de.martenschaefer.minecraft.worldgenupdater
package feature.definition.tree

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.IntProvider
import util._

trait TreeDecorator {
    val decoratorType: TreeDecoratorType[_]

    val process: TreeDecorator = this
}

object TreeDecorator {
    given Codec[TreeDecorator] = Registry[TreeDecoratorType[_]].dispatch(_.decoratorType, _.codec)
}

case class TreeDecoratorType[P <: TreeDecorator](val codec: Codec[P])

object TreeDecoratorType {
    given Registry[TreeDecoratorType[_]] = new SimpleRegistry(Identifier("minecraft", "tree_decorator_type"))

    private def register[P <: TreeDecorator](name: String, codec: Codec[P]): TreeDecoratorType[P] = {
        val decoratorType = TreeDecoratorType(codec)

        decoratorType.register(Identifier("minecraft", name))
        decoratorType
    }
}
