package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ElementNode }
import decorator.ConfiguredDecorator
import feature.placement.PlacedFeature
import feature.placement.definition.BiomePlacement
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import util.*
import cats.data.Writer

case object ArrayDecoratedFeature extends Feature(Codec[ArrayDecoratedFeatureConfig]) {
    override def process(config: ArrayDecoratedFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        @tailrec
        def loop(decorators: List[ConfiguredDecorator[_, _]], writer: FeatureProcessResult, index: Int): FeatureProcessResult =
            decorators match {
                case head :: tail =>
                    loop(tail, writer
                        .mapBoth((featureWarnings, feature) => head.decorator.process(head.config, feature, context)
                            .mapWritten(warnings2 => featureWarnings ::: warnings2
                                .map(_.withPrependedPath(ElementNode.Index(index)).withPrependedPath("decorators")
                                    .withPrependedPath("config"))).run),
                        index - 1)
                case Nil => writer.map(_ => writer.value)
            }

        loop(config.decorators.reverse, config.feature.feature.process(config.feature.config, context)
            .mapWritten(_.map(_.withPrependedPath("feature").withPrependedPath("config")))
            .map(feature => PlacedFeature(feature.feature, feature.modifiers.appended(BiomePlacement))), config.decorators.size - 1)
    }
}
