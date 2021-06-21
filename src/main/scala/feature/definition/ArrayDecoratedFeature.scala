package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.Codec
import de.martenschaefer.minecraft.worldgenupdater.decorator.ConfiguredDecorator
import feature.{ ConfiguredFeature, Feature, Features }

case object ArrayDecoratedFeature extends Feature(Codec[ArrayDecoratedFeatureConfig]) {
    override def process(config: ArrayDecoratedFeatureConfig): ConfiguredFeature[_, _] = {
        @tailrec
        def loop(decorators: List[ConfiguredDecorator[_, _]], feature: ConfiguredFeature[_, _]): ConfiguredFeature[_, _] =
            decorators match {
                case head :: tail =>
                    loop(tail, head.decorator.process(head.config, feature.feature.process(feature.config)))
                case Nil => feature.feature.process(feature.config)
            }

        loop(config.decorators.reverse, config.feature)
    }
}
