package de.martenschaefer.minecraft.worldgenupdater
package util

import java.io.{ IOException, InputStream, OutputStreamWriter, Writer }
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path, StandardOpenOption }
import java.util.Scanner
import scala.util.Using
import de.martenschaefer.data.serialization.{ ElementError, ElementNode, RecordParseError }

extension[T] (self: T) {
    def printlnDebug: T = {
        println("[debug] " + self)
        self
    }
}

extension[T] (self: List[T]) {
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

// for Codec.alternativesWithCustomError

def isMissingKeyError(error: ElementError, key: ElementNode): Boolean = error match {
    case RecordParseError.MissingKey(_, path) => path.lastOption.contains(key)
    case _ => false
}

def hasMissingKeyErrors(errors: List[ElementError], keys: List[ElementNode]): Boolean = {
    val lastPathNodes = errors.flatMap {
        case RecordParseError.MissingKey(_, path) => path.lastOption.toList
        case _ => List.empty
    }

    keys.forall(lastPathNodes.contains)
}

// IO

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
