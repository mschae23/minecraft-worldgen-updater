package de.martenschaefer.minecraft.worldgenupdater
package util

import scala.collection.immutable.ListMap
import de.martenschaefer.data.serialization.{ Codec, Element, ElementNode, RecordParseError, Result }
import de.martenschaefer.data.util._
import de.martenschaefer.data.util.DataResult._

case class BlockState(val name: Identifier, val properties: Map[String, Element])

object BlockState {
    given Codec[BlockState] with {
        def encodeElement(state: BlockState): Result[Element] = for {
            encodedName <- Codec[Identifier].encodeElement(state.name)
            result <- Success(Element.ObjectElement(ListMap(
                "Name" -> encodedName,
                "Properties" -> Element.ObjectElement(state.properties)
            )))
        } yield result

        def decodeElement(element: Element): Result[BlockState] = element match {
            case Element.ObjectElement(map) => for {
                decodedName <- Codec[Identifier].decodeElement(map.getOrElse("Name",
                    return Failure(Vector(RecordParseError.MissingKey(element, List(ElementNode.Name("Name")))))))
                result <- Success(BlockState(decodedName, map.getOrElse("Properties",
                    return Failure(Vector(RecordParseError.MissingKey(element, List(ElementNode.Name("Properties")))))) match {
                    case Element.ObjectElement(properties) => properties
                    case e => return Failure(Vector(RecordParseError.NotAnObject(e, List(ElementNode.Name("Properties")))))
                }))
            } yield result

            case _ => Failure(Vector(RecordParseError.NotAnObject(element, List())))
        }

        val lifecycle: Lifecycle = Lifecycle.Stable
    }
}
