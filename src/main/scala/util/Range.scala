package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.Codec

case class Range[+T](minInclusive: T, maxInclusive: T) derives Codec {
    override def toString: String = "[" + this.minInclusive + ", " + this.maxInclusive + "]"
}
