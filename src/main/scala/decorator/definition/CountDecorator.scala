package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.ConstantIntProvider
import cats.data.Writer

case object CountDecorator extends Decorator(Codec[CountDecoratorConfig]) {
    override def process(config: CountDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult =
        config.count.process match {
            case ConstantIntProvider(value) if !context.onlyUpdate && value == 1 => Writer(List.empty, feature)
            case ConstantIntProvider(value) if value == 0 => super.process(config, feature, context).mapBoth((warnings, feature) =>
                (ValidationError(path => s"$path: Count is zero; the decorated feature will never generate", List.empty) :: warnings, feature))

            case _ => super.process(if (context.onlyUpdate) config else config.process, feature, context)
        }
}
