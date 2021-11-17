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
        PlacedFeature(feature.feature, RarityFilterPlacement(config.chance) :: feature.modifiers).process(using context)
}
