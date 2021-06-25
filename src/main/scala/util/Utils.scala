package de.martenschaefer.minecraft.worldgenupdater
package util

extension [T](self: T) {
    def printlnDebug: T = {
        println(self)
        self
    }
}
