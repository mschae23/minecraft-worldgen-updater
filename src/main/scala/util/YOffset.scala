package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util._
import de.martenschaefer.data.util.Either._

enum YOffset(offset: Int) {
    case Fixed(absolute: Int) extends YOffset(absolute)
    case AboveBottom(aboveBottom: Int) extends YOffset(aboveBottom)
    case BelowTop(belowTop: Int) extends YOffset(belowTop)
}

object YOffset {
    given Codec[Fixed] = Codec.derived

    given Codec[AboveBottom] = Codec.derived

    given Codec[BelowTop] = Codec.derived

    private val errorMsg =
        (path: String) => s"$path must be an object with an \"absolute\", \"above_bottom\", or \"below_top\" field"

    given Codec[YOffset] = Codec.either(errorMsg)(using Codec.either(errorMsg)(using Codec[Fixed], Codec[AboveBottom]),
        Codec[BelowTop]).xmap(_ match {
        case Left(Left(value)) => value
        case Left(Right(value)) => value
        case Right(value) => value
    })(_ match {
        case offset: Fixed => Left(Left(offset))
        case offset: AboveBottom => Left(Right(offset))
        case offset: BelowTop => Right(offset)
    })
}
