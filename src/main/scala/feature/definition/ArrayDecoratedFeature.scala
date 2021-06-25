package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import scala.annotation.tailrec
import cats.data.Writer
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.minecraft.worldgenupdater.decorator.ConfiguredDecorator
import feature.{ ConfiguredFeature, Feature, Features }
import util._

case object ArrayDecoratedFeature extends Feature(Codec[ArrayDecoratedFeatureConfig]) {
    override def process(config: ArrayDecoratedFeatureConfig): FeatureProcessResult = {
        @tailrec
        def loop(decorators: List[ConfiguredDecorator[_, _]], writer: FeatureProcessResult): FeatureProcessResult =
            decorators match {
                case head :: tail =>
                    loop(tail, writer.value.feature.process(writer.value.config)
                        .mapBoth((warnings, feature) => head.decorator.process(head.config, feature)
                            .mapWritten(warnings2 => warnings.map(_.withPrependedPath("feature")) ::: warnings2
                            .map(_.withPrependedPath("decorator"))).mapWritten(_.map(_.withPrependedPath("config"))).run))
                case Nil => writer.map(_ => writer.value)
            }

        loop(config.decorators.reverse, Writer(List(), config.feature))
    }
}
