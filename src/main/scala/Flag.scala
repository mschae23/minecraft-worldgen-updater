package de.martenschaefer.minecraft.worldgenupdater

enum Flag {
    case UpdateOnly
    case AssumeYes
    case Colored
    case Recursive
    case Verbose

    def get(using flags: Flags): Boolean = flags(this)
}

type Flags = Map[Flag, Boolean]
