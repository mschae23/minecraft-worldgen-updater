package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.{ Codec, EitherError, ValidationError }
import de.martenschaefer.data.util.DataResult.*

enum Direction(val name: String) {
    case Down extends Direction("down")
    case Up extends Direction("up")
    case North extends Direction("north")
    case South extends Direction("south")
    case West extends Direction("west")
    case East extends Direction("east")
}

object Direction {
    private given EitherError =
        EitherError(path => s"$path should be \"down\", \"up\", \"north\", \"south\", \"west\", or \"east\"")

    given Codec[Direction] = Codec[String].flatXmap(_ match {
        case "down" => Success(Direction.Down)
        case "up" => Success(Direction.Up)
        case "north" => Success(Direction.North)
        case "south" => Success(Direction.South)
        case "west" => Success(Direction.West)
        case "east" => Success(Direction.East)

        case _ => Failure(List(ValidationError(path => EitherError.message(path), List.empty)))
    })(direction => Success(direction.name))
}
