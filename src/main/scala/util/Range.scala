package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.Codec

case class Range[+T](min: T, max: T) derives Codec {
    override def toString: String = "[" + this.min + ", " + this.max + "]"
}
