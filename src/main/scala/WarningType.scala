package de.martenschaefer.minecraft.worldgenupdater

import java.io.PrintStream

enum WarningType(val label: String) {
    case Warning extends WarningType("Warnings")
    case Error extends WarningType("Errors")
}
