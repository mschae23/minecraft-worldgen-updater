package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.{ Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.BlockPredicateFilterPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.BlockPos
import valueprovider.WouldSurviveBlockPredicate

case object BlockSurvivesFilterDecorator extends Decorator(Codec[BlockSurvivesFilterDecoratorConfig]) {
    override def process(config: BlockSurvivesFilterDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult = {
        PlacedFeature(feature.feature, BlockPredicateFilterPlacement(
            WouldSurviveBlockPredicate(BlockPos.ORIGIN, config.state)) :: feature.modifiers).process(using context)
    }
}
