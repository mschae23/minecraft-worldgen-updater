package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.{ ConfiguredDecorator, DecoratorConfig }

case class DecoratedDecoratorConfig(outer: ConfiguredDecorator[_, _],
                                    inner: ConfiguredDecorator[_, _]) extends DecoratorConfig derives Codec
