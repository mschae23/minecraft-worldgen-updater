package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.Codec

type DataPool[T] = Pool[Weighted.Present[T]]

object DataPool {
    def apply[T](entries: List[Weighted.Present[T]]) = Pool(entries)

    given [T: Codec]: Codec[DataPool[T]] = Codec[List[Weighted.Present[T]]].xmap(DataPool(_))(_.entries)
}
