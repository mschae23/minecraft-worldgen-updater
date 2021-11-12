package de.martenschaefer.minecraft.worldgenupdater
package feature.definition

import de.martenschaefer.data.serialization.{ Codec, ValidationError }
import decorator.Decorators
import decorator.definition.{ BlockFilterDecorator, BlockFilterDecoratorConfig }
import feature.{ Feature, FeatureProcessResult, Features }
import util.*
import valueprovider.{ AllOfBlockPredicate, BlockPredicate, TrueBlockPredicate }
import cats.data.Writer
import de.martenschaefer.minecraft.worldgenupdater.feature.placement.PlacedFeature

case object RandomPatchFeature extends Feature(Codec[RandomPatchFeatureConfig]) {
    override def process(config: RandomPatchFeatureConfig, context: FeatureUpdateContext): FeatureProcessResult = {
        if (config.old2.isDefined) {
            Features.DECORATED.process(DecoratedFeatureConfig(
                this.configure(RandomPatchFeatureConfig(config.tries, config.spreadXz, config.spreadY, config.feature, None)),
                Decorators.BLOCK_FILTER.configure(BlockFilterDecoratorConfig(AllOfBlockPredicate(List(
                    BlockFilterDecorator.updateOld1(
                        BlockFilterDecoratorConfig.Old1(config.old2.orNull.allowedOn, config.old2.orNull.disallowedOn.map(_.name),
                            BlockPos(0, -1, 0))
                    ), if (config.old2.orNull.onlyInAir.isDefined) {
                        if (config.old2.orNull.onlyInAir.get)
                            BlockPredicate.MATCHING_AIR else BlockPredicate.MATCHING_AIR_OR_WATER
                    } else TrueBlockPredicate)).process))), context)
        } else
            config.feature.feature.process(config.feature.config, context)
                .mapWritten(_.map(_.withPrependedPath("feature").withPrependedPath("config")))
                .mapBoth((warnings, feature) => (if (feature.modifiers.isEmpty) warnings else ValidationError(path =>
                    s"$path: Any decorators used in the nested feature in random_patch were REMOVED.", List.empty) :: warnings, feature)).map(feature =>
                PlacedFeature(this.configure(RandomPatchFeatureConfig(config.tries, config.spreadXz, config.spreadY,
                    feature.feature)), List.empty))
    }
}
