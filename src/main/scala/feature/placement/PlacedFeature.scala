package de.martenschaefer.minecraft.worldgenupdater
package feature.placement

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import de.martenschaefer.data.util.DataResult.*
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }

case class PlacedFeature(feature: ConfiguredFeature[_, _], modifiers: List[PlacementModifier]) {
    def process(using context: FeatureUpdateContext): FeatureProcessResult = {
        @tailrec
        def loop(decorators: List[PlacementModifier], writer: FeatureProcessResult, index: Int): FeatureProcessResult =
            decorators match {
                case head :: tail =>
                    loop(tail, writer.mapBoth((featureWarnings, feature) => head.process(feature)
                            .mapWritten(warnings2 => featureWarnings ::: warnings2
                                .map(_.withPrependedPath(ElementNode.Index(index)).withPrependedPath("placement"))).run),
                        index - 1)
                case Nil => writer
            }

        loop(this.modifiers.reverse, this.feature.feature.process(this.feature.config, context)
            .mapWritten(_.map(_.withPrependedPath("feature"))), this.modifiers.size - 1)
    }
}

object PlacedFeature {
    val placedFeatureCodec: Codec[PlacedFeature] = Codec.record[PlacedFeature] {
        val feature = Codec[ConfiguredFeature[_, _]].fieldOf("feature").forGetter[PlacedFeature](_.feature)
        val modifiers = Codec[List[PlacementModifier]].fieldOf("placement").forGetter[PlacedFeature](_.modifiers)

        Codec.build(PlacedFeature(feature.get, modifiers.get))
    }

    private val configuredFeatureCodec: Codec[PlacedFeature] = Codec[ConfiguredFeature[_, _]].flatXmap(
        feature => Success(PlacedFeature(feature, List.empty)))(feature => Failure(List(ValidationError(_ =>
        s"Placed feature failed to encode: $feature", List.empty))))

    given Codec[PlacedFeature] = placedFeatureCodec
        .flatOrElse(configuredFeatureCodec)

    Features // Init
}
