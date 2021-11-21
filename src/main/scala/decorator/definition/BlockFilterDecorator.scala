package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.definition.BlockFilterDecoratorConfig.Old1
import decorator.{ ConfiguredDecorator, Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.placement.PlacedFeature
import feature.placement.definition.BlockPredicateFilterPlacement
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, MatchingBlocksBlockPredicate, NotBlockPredicate, TrueBlockPredicate }
import cats.data.Writer

case object BlockFilterDecorator extends Decorator(Codec[BlockFilterDecoratorConfig]) {
    override def process(config: BlockFilterDecoratorConfig, feature: PlacedFeature, context: FeatureUpdateContext): FeatureProcessResult = {
        if (config.old1.isDefined)
            BlockPredicateFilterPlacement(updateOld1(config.old1.orNull).process).process(feature)(using context)
        else
            BlockPredicateFilterPlacement(config.predicate).process(feature)(using context)
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
