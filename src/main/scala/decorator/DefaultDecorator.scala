package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.serialization.{ Codec, Element }

case class DefaultDecorator(val encoded: Element) extends Decorator[DefaultDecoratorConfig](Codec[DefaultDecoratorConfig]) {
    override def toString: String = this.encoded match {
        case Element.ObjectElement(map) => map.get("type").flatMap(_ match {
            case Element.StringElement(value) => Some(value)
            case _ => None
        }).getOrElse(super.toString)
        case _ => super.toString
    }
}
