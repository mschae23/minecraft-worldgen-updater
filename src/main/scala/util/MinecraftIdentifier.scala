package de.martenschaefer.minecraft.worldgenupdater
package util

import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.data.util.Identifier

opaque type MinecraftIdentifier = Identifier

object MinecraftIdentifier {
    extension (self: MinecraftIdentifier) {
        def toIdentifier: Identifier = self

        def namespace: String = self.namespace
        def path: String = self.path
        def toString: String = self.toString
    }

    def apply(s: String): MinecraftIdentifier = Identifier(s)

    def apply(namespace: String, path: String): MinecraftIdentifier = Identifier(namespace, path)

    extension (self: Identifier) {
        def toMinecraft: MinecraftIdentifier = self
    }

    given Codec[MinecraftIdentifier] = Identifier.createCodec("minecraft").xmap(_.toMinecraft)(_.toIdentifier)
}
