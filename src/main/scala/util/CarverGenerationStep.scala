package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.DataResult.*

enum CarverGenerationStep(val name: String) {
    case Air extends CarverGenerationStep("air")
    case Liquid extends CarverGenerationStep("liquid")
}

object CarverGenerationStep {
    given Codec[CarverGenerationStep] = Codec[String].flatXmap {
        case "air" => Success(CarverGenerationStep.Air)
        case "liquid" => Success(CarverGenerationStep.Liquid)

        case _ => Failure(List(ValidationError(path => path, List.empty)))
    }(step => Success(step.name))
}
