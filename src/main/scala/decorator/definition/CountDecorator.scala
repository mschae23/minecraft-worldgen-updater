package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.Decorator
import feature.{ ConfiguredFeature, FeatureProcessResult }
import valueprovider.ConstantIntProvider

case object CountDecorator extends Decorator(Codec[CountDecoratorConfig]) {
    override def process(config: CountDecoratorConfig, feature: ConfiguredFeature[_, _]): FeatureProcessResult =
        config.count.process match {
            case ConstantIntProvider(value) if value == 1 => Writer(List(), feature)
            case ConstantIntProvider(value) if value == 0 => super.process(config, feature).mapBoth((warnings, feature) =>
                (ValidationError(path => s"$path: Count is zero; the decorated feature will never generate", List()) :: warnings, feature))

            case _ => super.process(config, feature)
        }
}
