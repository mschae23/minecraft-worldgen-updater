package de.martenschaefer.minecraft.worldgenupdater
package feature.placement

import de.martenschaefer.data.serialization.{ AlternativeError, Codec, ElementError, ElementNode, RecordParseError, ValidationError }
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

    given Codec[PlacedFeatureReference] = Codec.alternativesWithCustomError(
        ("for placed feature", placedFeatureCodec),
        ("for configured feature", configuredFeatureCodec),
        ("for placed feature reference", referenceCodec)) { subErrors =>
        val placedFeature = subErrors.find(subError => subError.label == "for placed feature").flatMap { subError =>
            subError.errors match {
                case RecordParseError.MissingKey(_, path) :: RecordParseError.MissingKey(_, path2) :: Nil =>
                    val lastPaths = List(path.last, path2.last)

                    if (lastPaths.contains(ElementNode.Name("feature")) && lastPaths.contains(ElementNode.Name("placement")))
                        None // Don't show this error if both the "feature" and "placement" fields are not present
                    else
                        Some(subError)

                case _ => Some(subError)
            }
        }

        val configuredFeature = subErrors.find(subError => subError.label == "for configured feature").flatMap { subError =>
            subError.errors match {
                case RecordParseError.MissingKey(_, path) :: RecordParseError.MissingKey(_, path2) :: Nil =>
                    val lastPaths = List(path.last, path2.last)

                    if (lastPaths.contains(ElementNode.Name("type")) && lastPaths.contains(ElementNode.Name("config")))
                        None // Don't show this error if both the "type" and "config" fields are not present
                    else
                        Some(subError)

                case _ => Some(subError)
            }
        }

        val reference = subErrors.find(subError => subError.label == "for placed feature reference").flatMap { subError =>
            subError.errors match {
                case RecordParseError.NotAString(_, _) :: Nil => None // Don't show this error if the element is not a String anyway

                case _ => Some(subError)
            }
        }

        List(placedFeature, configuredFeature, reference).flatMap(_.toList) match {
            case Nil => List(AlternativeError(subErrors))

            case subError :: Nil => subError.errors

            case errors => List(AlternativeError(errors))
        }
    }

    Features // Init

    def apply(reference: Either[Identifier, PlacedFeature]): PlacedFeatureReference =
        reference.fold(PlacedFeatureReference.Reference.apply, PlacedFeatureReference.Placed.apply)
}
