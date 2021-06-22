package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import scala.annotation.tailrec
import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import de.martenschaefer.minecraft.worldgenupdater.feature.{ ConfiguredFeature, Features }
import de.martenschaefer.minecraft.worldgenupdater.feature.definition.DecoratedFeatureConfig
import de.martenschaefer.minecraft.worldgenupdater.valueprovider.ConstantIntProvider
import decorator.{ ConfiguredDecorator, Decorator, Decorators }

case object RangeDecorator extends Decorator(Codec[RangeDecoratorConfig]) {
    override def process(config: RangeDecoratorConfig, feature: ConfiguredFeature[_, _]): FeatureProcessResult =
        processHeightReplacingDecorator(feature, Writer(List(), ConfiguredFeature(Features.DECORATED, DecoratedFeatureConfig(feature, ConfiguredDecorator
        (Decorators.RANGE, RangeDecoratorConfig(config.height.process))))))

    def processHeightReplacingDecorator(feature: ConfiguredFeature[_, _], otherwise: => FeatureProcessResult): FeatureProcessResult = {
        @tailrec
        def loop(feature: ConfiguredFeature[_, _]): Boolean = feature match {
            case ConfiguredFeature(Features.DECORATED, config: DecoratedFeatureConfig) =>
                config.decorator match {
                    case ConfiguredDecorator(Decorators.RANGE, _) => true
                    case _ => loop(config.feature);
                }
            case _ => false
        }

        if (loop(feature))
            Writer(List(), feature)
        else
            otherwise
    }
}
