package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.Codec
import decorator.definition.BlockFilterDecoratorConfig.Old1
import decorator.{ Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, MatchingBlocksBlockPredicate, NotBlockPredicate }
import cats.data.Writer

object BlockFilterDecorator extends Decorator(Codec[BlockFilterDecoratorConfig]) {
    override def process(config: BlockFilterDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult = {
        if (config.old1.isDefined)
            Features.DECORATED.process(DecoratedFeatureConfig(feature, Decorators.BLOCK_FILTER.configure(
                BlockFilterDecoratorConfig(updateOld1(config.old1.orNull)))), context)
        else if (!context.onlyUpdate)
            config.predicate match {
                case AllOfBlockPredicate(Nil) => Writer(List.empty, feature)

                case _ => super.process(config, feature, context)
            }
        else
            super.process(config, feature, context)
    }

    def updateOld1(old1: Old1): BlockPredicate = {
        if (old1.allowed.isEmpty && old1.disallowed.isEmpty)
            return AllOfBlockPredicate(List.empty)

        val allowed = MatchingBlocksBlockPredicate(old1.allowed, old1.offset)
        val disallowed = NotBlockPredicate(MatchingBlocksBlockPredicate(old1.disallowed, old1.offset))

        if (old1.disallowed.isEmpty)
            allowed
        else if (old1.allowed.isEmpty)
            disallowed
        else
            AllOfBlockPredicate(List(allowed, disallowed))
    }
}
