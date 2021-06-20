package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.registry.impl.SimpleRegistry
import de.martenschaefer.data.registry.Registry.register
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import decorator.definition._

class Decorator[DC <: DecoratorConfig](configCodec: Codec[DC]) {
    val codec: Codec[ConfiguredDecorator[DC, Decorator[DC]]] =
        configCodec.fieldOf("config").xmap(config => ConfiguredDecorator(this, config))(_.config)

    def configure(config: DC): ConfiguredDecorator[DC, Decorator[DC]] = ConfiguredDecorator(this, config)

    def process(decorator: ConfiguredDecorator[DC, Decorator[DC]]): ConfiguredDecorator[_, _] = decorator
}

object Decorator {
    given Registry[Decorator[_]] = new SimpleRegistry(Identifier("minecraft", "decorator"))
}
