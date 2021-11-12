package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.Result
import de.martenschaefer.data.serialization.{ Codec, EitherError, ValidationError }
import de.martenschaefer.data.util.DataResult.*

enum Direction(val name: String) {
    case Down extends Direction("down")
    case Up extends Direction("up")
    case North extends Direction("north")
    case South extends Direction("south")
    case West extends Direction("west")
    case East extends Direction("east")

    def isVertical: Boolean = this match {
        case Down => true
        case Up => true
        case _ => false
    }

    def isHorizontal: Boolean = !this.isVertical
}

object Direction {
    private val error: String => String = path =>
        s"$path should be \"down\", \"up\", \"north\", \"south\", \"west\", or \"east\""

    given Codec[Direction] = Codec[String].flatXmap {
        case "down" => Success(Direction.Down)
        case "up" => Success(Direction.Up)
        case "north" => Success(Direction.North)
        case "south" => Success(Direction.South)
        case "west" => Success(Direction.West)
        case "east" => Success(Direction.East)

        case _ => Failure(List(ValidationError(path => path, List.empty)))
    }(direction => Success(direction.name))
}

type VerticalDirection = Direction

object VerticalDirection {
    given Codec[VerticalDirection] = Codec[Direction].flatXmap(validateVertical)(validateVertical)

    import de.martenschaefer.data.util.DataResult.*

    def validateVertical(direction: Direction): Result[Direction] =
        if (direction.isVertical) Success(direction)
        else Failure(List(ValidationError(path =>
            s"$path: Direction is not vertical: ${direction.name}", List.empty)))
}
