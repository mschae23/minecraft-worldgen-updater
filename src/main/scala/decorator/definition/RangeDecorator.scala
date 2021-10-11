package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import scala.annotation.tailrec
import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.{ ConfiguredFeature, Features, FeatureProcessResult }
import feature.definition.DecoratedFeatureConfig
import valueprovider.ConstantIntProvider

case object RangeDecorator extends Decorator(Codec[RangeDecoratorConfig]) {
    override def process(config: RangeDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult =
        processHeightReplacingDecorator(feature, Writer(List.empty, ConfiguredFeature(Features.DECORATED, DecoratedFeatureConfig(feature,
            ConfiguredDecorator(Decorators.RANGE, RangeDecoratorConfig(if (context.onlyUpdate) config.height else config.height.process))))), context)

    def processHeightReplacingDecorator(feature: ConfiguredFeature[_, _], otherwise: => FeatureProcessResult, context: FeatureUpdateContext): FeatureProcessResult = {
        @tailrec
        def loop(feature: ConfiguredFeature[_, _]): Boolean = feature match {
            case ConfiguredFeature(Features.DECORATED, config: DecoratedFeatureConfig) =>
                config.decorator match {
                    case ConfiguredDecorator(Decorators.RANGE, _) => true
                    case ConfiguredDecorator(Decorators.HEIGHTMAP, _) => true
                    case _ => loop(config.feature)
                }
            case _ => false
        }

        if (!context.onlyUpdate && loop(feature))
            Writer(List.empty, feature)
        else
            otherwise
    }
}
