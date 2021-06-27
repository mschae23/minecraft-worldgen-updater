package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import scala.annotation.tailrec
import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode }
import de.martenschaefer.minecraft.worldgenupdater.decorator.ConfiguredDecorator
import feature.{ ConfiguredFeature, Feature, FeatureProcessResult, Features }
import util._

case object ArrayDecoratedFeature extends Feature(Codec[ArrayDecoratedFeatureConfig]) {
    override def process(config: ArrayDecoratedFeatureConfig): FeatureProcessResult = {
        @tailrec
        def loop(decorators: List[ConfiguredDecorator[_, _]], writer: FeatureProcessResult, index: Int): FeatureProcessResult =
            decorators match {
                case head :: tail =>
                    loop(tail, writer
                        .mapBoth((featureWarnings, feature) => head.decorator.process(head.config, feature)
                            .mapWritten(warnings2 => featureWarnings ::: warnings2
                                .map(_.withPrependedPath(ElementNode.Index(index)).withPrependedPath("decorators")
                                .withPrependedPath("config"))).run),
                        index - 1)
                case Nil => writer.map(_ => writer.value)
            }

        loop(config.decorators.reverse, config.feature.feature.process(config.feature.config)
            .mapWritten(_.map(_.withPrependedPath("feature").withPrependedPath("config"))), config.decorators.size - 1)
    }
}
