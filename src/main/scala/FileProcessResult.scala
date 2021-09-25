package de.martenschaefer.minecraft.worldgenupdater

import de.martenschaefer.data.serialization.ElementError

enum FileProcessResult {
    case Normal
    case Warnings(val warnings: List[ElementError])
    case Errors(val errors: List[ElementError])

    def +(other: FileProcessResult): FileProcessResult = other match {
        case Errors(errors) => this match {
            case Errors(errors2) => Errors(errors2 ::: errors)
            case _ => Errors(errors)
        }
        case _ if this.isInstanceOf[Errors] => this
        case Warnings(warnings) => this match {
            case Warnings(warnings2) => Warnings(warnings2 ::: warnings)
            case _ => Warnings(warnings)
        }
        case _ => this
    }
}
