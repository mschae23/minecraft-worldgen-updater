package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.data.util.DataResult.*

enum VerticalSurfaceType(val name: String) {
    case Ceiling extends VerticalSurfaceType("ceiling")
    case Floor extends VerticalSurfaceType("floor")
}

object VerticalSurfaceType {
    given Codec[VerticalSurfaceType] = Codec[String].flatXmap {
        case "ceiling" => Success(VerticalSurfaceType.Ceiling)
        case "floor" => Success(VerticalSurfaceType.Floor)

        case _ => Failure(List(ValidationError(path => path, List.empty)))
    }(surface => Success(surface.name))
}
