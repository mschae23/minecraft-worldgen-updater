package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import scala.annotation.tailrec
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.RarityFilterPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.ConstantIntProvider
import cats.catsInstancesForId
import cats.data.Writer

case object ChanceDecorator extends Decorator(Codec[ChanceDecoratorConfig]) {
    override def process(config: ChanceDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult =
        config.chance match {
            case 1 if !context.onlyUpdate => Writer.value(feature) // don't add this decorator if chance == 1
            case 0 => PlacedFeature(feature.feature, RarityFilterPlacement(config.chance) :: feature.modifiers).process(using context)
                .mapBoth((warnings, feature) => (ValidationError(path =>
                    s"$path: Chance is zero; Minecraft will probably crash", List.empty) :: warnings, feature))

            case _ => PlacedFeature(feature.feature, RarityFilterPlacement(config.chance) :: feature.modifiers).process(using context)
                // .map(mergeChanceDecorators(_, context)) // TODO
        }

    def mergeChanceDecorators(feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): ConfiguredFeature[_, _] = {
        @tailrec
        def loop(feature: ConfiguredFeature[_, _], previous: Option[(ConfiguredFeature[_, _], Int)]): ConfiguredFeature[_, _] = feature match {
            case ConfiguredFeature(Features.DECORATED, config: DecoratedFeatureConfig) => config.decorator match {
                case ConfiguredDecorator(Decorators.CHANCE, chanceConfig: ChanceDecoratorConfig) => previous match {
                    case Some((feature, chance)) => Features.DECORATED.configure(DecoratedFeatureConfig(config.feature,
                        Decorators.CHANCE.configure(ChanceDecoratorConfig(chance * chanceConfig.chance))))

                    case None => loop(config.feature, Some(feature, chanceConfig.chance))
                }

                case ConfiguredDecorator(_, _) => previous match {
                    case None => feature
                    case Some((feature, _)) => feature
                }
            }

            case ConfiguredFeature(_, _) => previous match {
                case None => feature
                case Some((feature, _)) => feature
            }
        }

        if (context.onlyUpdate)
            feature
        else
            loop(feature, None)
    }
}
