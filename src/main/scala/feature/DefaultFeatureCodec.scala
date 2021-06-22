package de.martenschaefer.minecraft.worldgenupdater
package feature

import de.martenschaefer.data.registry.Registry
import de.martenschaefer.data.serialization.{ Codec, Element, Result }
import de.martenschaefer.data.util.Either._

class DefaultFeatureCodec(codec: Codec[ConfiguredFeature[_, _]]) extends Codec[ConfiguredFeature[_, _]] {
    override def encodeElement(value: ConfiguredFeature[_, _]): Result[Element] = value match {
        case ConfiguredFeature(DefaultFeature(element), _) => Right(element)
        case feature => codec.encodeElement(feature)
    }

    override def decodeElement(element: Element): Result[ConfiguredFeature[_, _]] = codec.decodeElement(element) match {
        case Left(errors) => element match {
            case Element.ObjectElement(map) =>
                if (map.contains("type") && map.contains("config"))
                    if (errors.exists(_.isInstanceOf[Registry.UnknownRegistryIdError]))
                        Right(ConfiguredFeature(DefaultFeature(element), null))
                    else
                        Left(errors)
                else
                    Left(errors)

            case Element.StringElement(_) => Right(ConfiguredFeature(DefaultFeature(element), null))

            case _ => Left(errors)
        }

        case result => result
    }

    override val lifecycle = codec.lifecycle
}
