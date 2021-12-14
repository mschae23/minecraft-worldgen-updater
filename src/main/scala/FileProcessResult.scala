package de.martenschaefer.minecraft.worldgenupdater

import de.martenschaefer.data.serialization.ElementError

enum FileProcessResult(val warningType: WarningType) {
    case Normal extends FileProcessResult(WarningType.Okay)
    case Warnings(val warnings: List[ElementError]) extends FileProcessResult(WarningType.Warning)
    case Errors(val errors: List[ElementError]) extends FileProcessResult(WarningType.Error)

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
