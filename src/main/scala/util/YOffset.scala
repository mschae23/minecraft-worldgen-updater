package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.*
import de.martenschaefer.data.util.DataResult.*

enum YOffset(offset: Int) {
    case Fixed(absolute: Int) extends YOffset(absolute)
    case AboveBottom(aboveBottom: Int) extends YOffset(aboveBottom)
    case BelowTop(belowTop: Int) extends YOffset(belowTop)
}

object YOffset {
    given Codec[Fixed] = Codec.derived

    private val fixedCodec: Codec[YOffset] = Codec[Fixed].flatXmap(Success(_))(_ match {
        case fixed: Fixed => Success(fixed)
        case _ => Failure(List(ValidationError(path => s"$path: YOffset is not absolute", List.empty)))
    })

    given Codec[AboveBottom] = Codec.derived

    private val aboveBottomCodec: Codec[YOffset] = Codec[AboveBottom].flatXmap(Success(_))(_ match {
        case aboveBottom: AboveBottom => Success(aboveBottom)
        case _ => Failure(List(ValidationError(path => s"$path: YOffset is not above bottom", List.empty)))
    })

    given Codec[BelowTop] = Codec.derived

    private val belowTopCodec: Codec[YOffset] = Codec[BelowTop].flatXmap(Success(_))(_ match {
        case belowTop: BelowTop => Success(belowTop)
        case _ => Failure(List(ValidationError(path => s"$path: YOffset is not below top", List.empty)))
    })

    given Codec[YOffset] = Codec.alternatives(List(fixedCodec, aboveBottomCodec, belowTopCodec))
}
