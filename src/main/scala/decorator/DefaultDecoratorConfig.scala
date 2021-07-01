package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.serialization.Codec

case class DefaultDecoratorConfig() extends DecoratorConfig {
    override def toString: String = "DefaultDecoratorConfig"
}

object DefaultDecoratorConfig {
    val INSTANCE = DefaultDecoratorConfig()

    given Codec[DefaultDecoratorConfig] = Codec.unit(() => INSTANCE)
}


