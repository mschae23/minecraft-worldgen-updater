package de.martenschaefer.minecraft.worldgenupdater
package util

import scala.collection.immutable.ListMap
import de.martenschaefer.data.Result
import de.martenschaefer.data.serialization.{ Codec, Element, ElementNode, RecordParseError }
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
                decodedName <- Identifier.createCodec("minecraft").decodeElement(map.getOrElse("Name",
                    return Failure(List(RecordParseError.MissingKey(element, List(ElementNode.Name("Name")))))))
                result <- Success(BlockState(decodedName, map.getOrElse("Properties",
                    Element.ObjectElement(Map.empty)) match {
                    case Element.ObjectElement(properties) => properties
                    case e => return Failure(List(RecordParseError.NotAnObject(e, List(ElementNode.Name("Properties")))))
                }))
            } yield result

            case _ => Failure(List(RecordParseError.NotAnObject(element, List())))
        }

        val lifecycle: Lifecycle = Lifecycle.Stable
    }
}
