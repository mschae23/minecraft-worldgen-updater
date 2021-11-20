package de.martenschaefer.minecraft.worldgenupdater
package feature.placement

import de.martenschaefer.data.Result
import de.martenschaefer.data.serialization.{ Codec, Element }
import de.martenschaefer.data.util.{ Identifier, Lifecycle }
import feature.definition.PlacedFeatureReferenceFeatureConfig
import feature.{ ConfiguredFeature, Features }

case class PlacedFeatureCodec(decoratedCodec: Codec[PlacedFeature]) extends Codec[PlacedFeature] {
    override def encodeElement(value: PlacedFeature): Result[Element] = value.feature match {
        case ConfiguredFeature(Features.PLACED_FEATURE_REFERENCE, PlacedFeatureReferenceFeatureConfig(reference)) =>
            Codec[Identifier].encodeElement(reference)
        case _ => this.decoratedCodec.encodeElement(value)
    }

    override def decodeElement(element: Element): Result[PlacedFeature] = element match {
        case Element.StringElement(_) =>
            Codec[Identifier].decodeElement(element)
                .map(reference => PlacedFeature(Features.PLACED_FEATURE_REFERENCE.configure(
                    PlacedFeatureReferenceFeatureConfig(reference)), List.empty))
        case _ => this.decoratedCodec.decodeElement(element)
    }

    override val lifecycle: Lifecycle = this.decoratedCodec.lifecycle
}
