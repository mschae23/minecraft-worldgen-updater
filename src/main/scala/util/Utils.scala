package de.martenschaefer.minecraft.worldgenupdater
package util

import java.io.{ IOException, InputStream, OutputStreamWriter, Writer }
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path, StandardOpenOption }
import java.util.Scanner
import scala.util.Using

extension [T](self: T) {
    def printlnDebug: T = {
        println("[debug] " + self)
        self
    }
}

extension [T](self: List[T]) {
    def uniquePairs: List[(T, T)] = for {
        (x, idxX) <- self.zipWithIndex
        (y, idxY) <- self.zipWithIndex
        if idxX < idxY
    } yield (x, y)
}

def colored(text: String, color: String)(using flags: Map[Flag, Boolean]): String = {
    if (!flags(Flag.Colored))
        text
    else
        color + text + Console.RESET
}

@throws[IOException]
def read(file: Path): String = {
    Using.Manager { use =>
        val in: InputStream = use(Files.newInputStream(file))
        val scanner: Scanner = use(new Scanner(in, StandardCharsets.UTF_8.name))

        scanner.useDelimiter("\\A").next()
    }.get
}

@throws[IOException]
def write(file: Path, content: String): Unit = {
    Using.Manager { use =>
        val out: Writer = use(OutputStreamWriter(Files.newOutputStream(file,
            StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)))

        out.write(content)
    }
}
