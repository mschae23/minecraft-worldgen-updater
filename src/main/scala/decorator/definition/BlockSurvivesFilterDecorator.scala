package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.{ Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import util.BlockPos
import valueprovider.WouldSurviveBlockPredicate

case object BlockSurvivesFilterDecorator extends Decorator(Codec[BlockSurvivesFilterDecoratorConfig]) {
    override def process(config: BlockSurvivesFilterDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult = {
        Features.DECORATED.process(DecoratedFeatureConfig(feature, Decorators.BLOCK_FILTER.configure(
            BlockFilterDecoratorConfig(WouldSurviveBlockPredicate(BlockPos.ORIGIN, config.state)))), context)
    }
}
