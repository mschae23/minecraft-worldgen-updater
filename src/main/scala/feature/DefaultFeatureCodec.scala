package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.Result
import de.martenschaefer.data.serialization.{ Codec, Element }
import de.martenschaefer.data.util.DataResult._
import de.martenschaefer.data.util.Lifecycle

class DefaultFeatureCodec(codec: Codec[ConfiguredFeature[_, _]]) extends Codec[ConfiguredFeature[_, _]] {
    override def encodeElement(value: ConfiguredFeature[_, _]): Result[Element] = value match {
        case ConfiguredFeature(DefaultFeature(element), _) => Success(element)
        case _ => codec.encodeElement(value)
    }

    override def decodeElement(element: Element): Result[ConfiguredFeature[_, _]] = codec.decodeElement(element) match {
        case Failure(errors, lifecycle) => element match {
            case Element.ObjectElement(map) =>
                if (map.contains("type") && map.contains("config"))
                    if (errors.exists(error => error.isInstanceOf[Registry.UnknownRegistryIdError]
                        && (error.asInstanceOf[Registry.UnknownRegistryIdError].element == map("type"))))
                        Success(ConfiguredFeature(DefaultFeature(element), null), lifecycle)
                    else
                        Failure(errors, lifecycle)
                else
                    Failure(errors, lifecycle)

            case Element.StringElement(_) => Success(ConfiguredFeature(DefaultFeature(element), null), lifecycle)

            case _ => Failure(errors, lifecycle)
        }

        case result => result
    }

    override val lifecycle = codec.lifecycle
}
