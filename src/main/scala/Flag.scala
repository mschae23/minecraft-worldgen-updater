package de.martenschaefer.minecraft.worldgenupdater

enum Flag {
    case UpdateOnly
    case AssumeYes
    case Colored
}

type Flags = Map[Flag, Boolean]
