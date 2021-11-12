package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.EnvironmentScanPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.TrueBlockPredicate
import cats.catsInstancesForId
import cats.data.Writer

case object EnvironmentScanDecorator extends Decorator(Codec[EnvironmentScanDecoratorConfig]) {
    override def process(config: EnvironmentScanDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult = {
        if (context.onlyUpdate)
            this.updateBasic(config, feature, context).flatMap(feature => addMaxStepsWarning(config, feature))
        else
            this.updateBasic(config, feature, context)
                .flatMap(feature => addMaxStepsWarning(EnvironmentScanDecoratorConfig(
                    config.directionOfSearch, config.targetCondition.process, config.maxSteps), feature))
    }

    def updateBasic(config: EnvironmentScanDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        PlacedFeature(feature.feature, EnvironmentScanPlacement(config.directionOfSearch, config.targetCondition,
            TrueBlockPredicate, config.maxSteps) :: feature.modifiers).process(using context)

    def addMaxStepsWarning(config: EnvironmentScanDecoratorConfig, feature: PlacedFeature): FeatureProcessResult = {
        if (config.maxSteps < 1 || config.maxSteps > 32)
            Writer(List(ValidationError(path => s"$path: Max steps must be between 1 and 32: ${config.maxSteps}", List.empty)
                .withPrependedPath("max_steps").withPrependedPath("config")), feature)
        else
            Writer.value(feature)
    }
}
