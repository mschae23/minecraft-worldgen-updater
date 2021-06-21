package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.serialization.{ Codec, Element, Result }
import de.martenschaefer.data.util.Either._

class DefaultDecoratorCodec(codec: Codec[ConfiguredDecorator[_, _]]) extends Codec[ConfiguredDecorator[_, _]] {
    override def encodeElement(value: ConfiguredDecorator[_, _]): Result[Element] = value match {
        case ConfiguredDecorator(DefaultDecorator(element), _) => Right(element)
        case decorator => codec.encodeElement(decorator)
    }

    override def decodeElement(element: Element): Result[ConfiguredDecorator[_, _]] = codec.decodeElement(element) match {
        case Left(errors) => element match {
            case Element.ObjectElement(map) =>
                if (map.contains("type") && map.contains("config"))
                    if (errors.exists(_.isInstanceOf[Registry.UnknownRegistryIdError]))
                        Right(ConfiguredDecorator(DefaultDecorator(element), null))
                    else
                        Left(errors)
                else
                    Left(errors)

            case _ => Left(errors)
        }

        case result => result
    }

    override val lifecycle = codec.lifecycle
}
