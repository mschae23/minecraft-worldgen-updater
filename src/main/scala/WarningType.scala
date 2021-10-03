package de.martenschaefer.minecraft.worldgenupdater

import java.io.PrintStream

enum WarningType(val label: String, val color: String) {
    case Okay extends WarningType("No warnings", Console.GREEN)
    case Warning extends WarningType("Warnings", Console.YELLOW)
    case Error extends WarningType("Errors", Console.RED)
}
