package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import cats.catsInstancesForId
import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }

case object EnvironmentScanDecorator extends Decorator(Codec[EnvironmentScanDecoratorConfig]) {
    override def process(config: EnvironmentScanDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult = {
        if (context.onlyUpdate)
            super.process(config, feature, context).flatMap(feature => addMaxStepsWarning(config, feature))
        else
            super.process(config, feature, context)
                .flatMap(feature => addMaxStepsWarning(EnvironmentScanDecoratorConfig(
                    config.directionOfSearch, config.targetCondition.process, config.maxSteps), feature))
    }

    def addMaxStepsWarning(config: EnvironmentScanDecoratorConfig, feature: ConfiguredFeature[_, _]): FeatureProcessResult = {
        if (config.maxSteps < 1 || config.maxSteps > 32)
            Writer(List(ValidationError(path => s"$path: Max steps must be between 1 and 32: ${config.maxSteps}", List.empty)
            .withPrependedPath("max_steps").withPrependedPath("config")), feature)
        else
            Writer(List.empty, feature)
    }
}
