package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.serialization.{ Codec, Element }

case class DefaultDecorator(val encoded: Element) extends Decorator[DefaultDecoratorConfig](Codec[DefaultDecoratorConfig])
