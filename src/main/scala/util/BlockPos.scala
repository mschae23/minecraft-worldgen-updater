package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import de.martenschaefer.data.util.DataResult

case class BlockPos(x: Int, y: Int, z: Int)

object BlockPos {
    given Codec[BlockPos] = Codec[List[Int]].flatXmap(list => if (list.size >= 3)
        DataResult.Success(BlockPos(list(0), list(1), list(2))) else DataResult.Failure(List(ValidationError(
        path => s"$path: BlockPos must be a list of 3 ints", List.empty))))(pos => DataResult.Success(List(pos.x, pos.y, pos.z)))
}
