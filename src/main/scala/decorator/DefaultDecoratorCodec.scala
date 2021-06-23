package de.martenschaefer.minecraft.worldgenupdater
package decorator

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.serialization.{ Codec, Element, Result }
import de.martenschaefer.data.util.DataResult._

class DefaultDecoratorCodec(codec: Codec[ConfiguredDecorator[_, _]]) extends Codec[ConfiguredDecorator[_, _]] {
    override def encodeElement(value: ConfiguredDecorator[_, _]): Result[Element] = value match {
        case ConfiguredDecorator(DefaultDecorator(element), _) => Success(element, codec.lifecycle)
        case decorator => codec.encodeElement(decorator)
    }

    override def decodeElement(element: Element): Result[ConfiguredDecorator[_, _]] = codec.decodeElement(element) match {
        case Failure(errors, lifecycle) => element match {
            case Element.ObjectElement(map) =>
                if (map.contains("type") && map.contains("config"))
                    if (errors.exists(_.isInstanceOf[Registry.UnknownRegistryIdError]))
                        Success(ConfiguredDecorator(DefaultDecorator(element), null), lifecycle)
                    else
                        Failure(errors, lifecycle)
                else
                    Failure(errors, lifecycle)

            case _ => Failure(errors, lifecycle)
        }

        case result => result
    }

    override val lifecycle = codec.lifecycle
}
