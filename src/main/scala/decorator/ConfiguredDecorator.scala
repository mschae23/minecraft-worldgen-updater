package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._

case class ConfiguredDecorator[DC <: DecoratorConfig, D <: Decorator[DC]](val decorator: D, val config: DC)

object ConfiguredDecorator {
    // given Registry[ConfiguredDecorator[_, _]] = new SimpleRegistry(Identifier("minecraft", "configured_decorator"))

    given Codec[ConfiguredDecorator[_, _]] = DefaultDecoratorCodec(Registry[Decorator[_]].dispatch(_.decorator, _.codec))

    Decorators // Init
}
