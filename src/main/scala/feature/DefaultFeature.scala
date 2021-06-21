package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.serialization.{ Codec, Element }

case class DefaultFeature(val encoded: Element) extends Feature[DefaultFeatureConfig](Codec[DefaultFeatureConfig]) {
    override def toString: String = this.encoded match {
        case Element.ObjectElement(map) => map.get("type").flatMap(_ match {
            case Element.StringElement(value) => Some(value)
            case _ => None
        }).getOrElse(super.toString)
        case _ => super.toString
    }
}
