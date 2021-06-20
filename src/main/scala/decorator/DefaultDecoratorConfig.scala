package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.serialization.Codec

case class DefaultDecoratorConfig() extends DecoratorConfig

object DefaultDecoratorConfig {
    val INSTANCE = DefaultDecoratorConfig()

    given Codec[DefaultDecoratorConfig] = Codec.unit(() => INSTANCE)
}


