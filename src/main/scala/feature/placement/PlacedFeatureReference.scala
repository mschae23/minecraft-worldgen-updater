package de.martenschaefer.minecraft.worldgenupdater
package feature.placement

import de.martenschaefer.data.serialization.{ Codec, ElementError, ValidationError }
import de.martenschaefer.data.util.DataResult.*
import de.martenschaefer.data.util.Identifier
import feature.{ ConfiguredFeature, Features }
import cats.data.Writer

enum PlacedFeatureReference {
    case Reference(feature: Identifier)
    case Configured(feature: ConfiguredFeature[_, _])
    case Placed(feature: PlacedFeature)

    def process(using context: FeatureUpdateContext): ProcessResult[Either[Identifier, PlacedFeature]] = this match {
        case Reference(feature) => Writer.value(Left(feature))
        case Configured(feature) => feature.feature.process(feature.config, context).map(Right.apply)
        case Placed(feature) => feature.process.map(Right.apply)
    }

    def getPostProcessWarnings(using context: FeatureUpdateContext): List[ElementError] = this match {
        case Reference(_) => List.empty
        case Configured(feature) => feature.feature.getPostProcessWarnings(feature.config, context)
        case Placed(feature) => feature.getPostProcessWarnings
    }
}

object PlacedFeatureReference {
    private val placedFeatureCodec: Codec[PlacedFeatureReference] = Codec[PlacedFeature].flatXmap(
        feature => Success(PlacedFeatureReference.Placed(feature))) {
        case Placed(feature) => Success(feature)
        case feature => Failure(List(ValidationError(path => s"$path: Not a placed feature: $feature")))
    }

    private val configuredFeatureCodec: Codec[PlacedFeatureReference] = Codec[ConfiguredFeature[_, _]].flatXmap(
        feature => Success(PlacedFeatureReference.Configured(feature)))(feature => Failure(List(ValidationError(path =>
        s"$path: Configured feature wasn't processed to a placed feature: $feature"))))

    private val referenceCodec: Codec[PlacedFeatureReference] = Codec[Identifier].flatXmap(
        reference => Success(PlacedFeatureReference.Reference(reference))) {
        case Reference(reference) => Success(reference)
        case reference => Failure(List(ValidationError(path => s"$path: Not a placed feature reference: $reference")))
    }

    given Codec[PlacedFeatureReference] = Codec.alternatives(List(placedFeatureCodec, configuredFeatureCodec, referenceCodec))

    Features // Init

    def apply(reference: Either[Identifier, PlacedFeature]): PlacedFeatureReference =
        reference.fold(PlacedFeatureReference.Reference.apply, PlacedFeatureReference.Placed.apply)
}
