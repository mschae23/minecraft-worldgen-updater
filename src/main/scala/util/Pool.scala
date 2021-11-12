package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.Codec

class Pool[T <: Weighted] private(val totalWeight: Int, val entries: List[T]) {
    def this(entries: List[T]) =
        this(entries.foldLeft(0)((weight, t) => weight + t.weight.value), entries)

    export this.entries.isEmpty
}

object Pool {
    def apply[T <: Weighted](entries: List[T]) = new Pool(entries)

    given [T <: Weighted: Codec]: Codec[Pool[T]] = Codec[List[T]].xmap(Pool(_))(_.entries)
}

trait Weighted {
    val weight: Weight
}

object Weighted {
    case class Present[T](data: T, override val weight: Weight) extends Weighted derives Codec

    case class Absent(override val weight: Weight) extends Weighted derives Codec
}

case class Weight(value: Int)

object Weight {
    given Codec[Weight] = Codec[Int].xmap(Weight(_))(_.value)
}
