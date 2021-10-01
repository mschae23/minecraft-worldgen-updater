package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.{ Decorator, Decorators, DefaultDecoratorConfig }
import feature.{ ConfiguredFeature, FeatureProcessResult }

case object Spread32AboveDecorator extends Decorator(Codec[DefaultDecoratorConfig]) {
    override def process(config: DefaultDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult =
        Decorators.CHANCE.process(ChanceDecoratorConfig(32), feature, context)
}
