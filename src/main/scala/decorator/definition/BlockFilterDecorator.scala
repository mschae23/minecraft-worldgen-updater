package de.martenschaefer.minecraft.worldgenupdater
package decorator.definition

import cats.data.Writer
import de.martenschaefer.data.serialization.{ Codec, ElementNode, ValidationError }
import decorator.definition.BlockFilterDecoratorConfig.Old1
import decorator.{ Decorator, Decorators }
import feature.definition.DecoratedFeatureConfig
import feature.{ ConfiguredFeature, FeatureProcessResult, Features }
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, MatchingBlocksBlockPredicate, NotBlockPredicate, TrueBlockPredicate }

case object BlockFilterDecorator extends Decorator(Codec[BlockFilterDecoratorConfig]) {
    override def process(config: BlockFilterDecoratorConfig, feature: ConfiguredFeature[_, _], context: FeatureUpdateContext): FeatureProcessResult = {
        if (config.old1.isDefined)
            Features.DECORATED.process(DecoratedFeatureConfig(feature, Decorators.BLOCK_FILTER.configure(
                BlockFilterDecoratorConfig(updateOld1(config.old1.orNull).process))), context)
        else if (!context.onlyUpdate)
            config.predicate.process match {
                case TrueBlockPredicate => Writer(List.empty, feature)
                case NotBlockPredicate(TrueBlockPredicate) => Writer(List(
                    ValidationError(path => s"$path: Block filter uses not(true) predicate; "
                        + "the decorated feature will never generate", List(ElementNode.Name("config"),
                        ElementNode.Name("predicate")))), Features.DECORATED.configure(
                    DecoratedFeatureConfig(feature, this.configure(config))))

                case predicate => Writer(List.empty, Features.DECORATED.configure(
                    DecoratedFeatureConfig(feature, this.configure(
                        BlockFilterDecoratorConfig(predicate)))))
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
